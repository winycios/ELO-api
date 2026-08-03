package br.com.elo.eloapi.service.notificacao;

import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.notificacao.*;
import br.com.elo.eloapi.repository.NotificacaoOutboxRepository;
import br.com.elo.eloapi.service.notificacao.canal.ComandoEnvioNotificacao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificacaoOutboxService {

    private final NotificacaoOutboxRepository outboxRepository;

    @Value("${elo.notification.outbox.processing-timeout-seconds:300}")
    private long timeoutProcessamentoSegundos;

    @Value("${elo.notification.outbox.retry-base-seconds:5}")
    private long retryBaseSegundos;

    @Value("${elo.notification.outbox.retry-max-seconds:3600}")
    private long retryMaxSegundos;

    @Transactional
    public List<Long> reservarPendentes(Integer maxTentativas) {
        LocalDateTime agora = LocalDateTime.now();
        List<NotificacaoOutbox> pendentes = outboxRepository.reservarPendentes(agora, agora.minusSeconds(timeoutProcessamentoSegundos), maxTentativas);
        pendentes.forEach(entrega -> {
            entrega.setStatus(StatusEnvioNotificacao.PROCESSANDO);
            entrega.setDtProcessandoDesde(agora);
        });
        outboxRepository.saveAll(pendentes);
        return pendentes.stream().map(NotificacaoOutbox::getId).toList();
    }

    @Transactional(readOnly = true)
    public ComandoEnvioNotificacao prepararEnvio(Long outboxId) {
        NotificacaoOutbox entrega = buscar(outboxId);
        if (entrega.getStatus() != StatusEnvioNotificacao.PROCESSANDO) {
            throw new IllegalStateException("Entrega não está reservada para processamento.");
        }

        Notificacao notificacao = entrega.getNotificacao();
        DispositivoUsuario dispositivo = entrega.getDispositivo();
        if (entrega.getCanal() == CanalNotificacao.PUSH && (dispositivo == null || !Boolean.TRUE.equals(dispositivo.getAtivo()))) {
            throw new br.com.elo.eloapi.service.notificacao.canal.FalhaPermanenteEnvioException("Dispositivo de destino está inativo.");
        }

        Map<String, String> dados = Map.of("tipo", notificacao.getTipo().name(), "orcamentoId", notificacao.getOrcamento().getId().toString(), "notificacaoId", notificacao.getId().toString(), "rota", "/orcamentos/" + notificacao.getOrcamento().getId(), "versao", "1");
        return new ComandoEnvioNotificacao(
                entrega.getId(),
                entrega.getCanal(),
                notificacao.getDestinatario().getId(),
                notificacao.getDestinatario().getEmail(),
                dispositivo == null ? null : dispositivo.getId(),
                dispositivo == null ? null : dispositivo.getIdentificadorFcm(),
                dispositivo == null ? null : dispositivo.getTipoIdentificador(),
                notificacao.getTipo(),
                notificacao.getOrcamento().getId(),
                notificacao.getId(),
                notificacao.getTitulo(),
                notificacao.getMensagem(),
                dados
        );
    }

    @Transactional
    public void concluir(Long outboxId, String codigoMensagemProvedor) {
        NotificacaoOutbox entrega = buscar(outboxId);
        entrega.setStatus(StatusEnvioNotificacao.ENVIADO);
        entrega.setCodigoMensagemProvedor(codigoMensagemProvedor);
        entrega.setDtProcessamento(LocalDateTime.now());
        entrega.setDtProcessandoDesde(null);
        entrega.setUltimoErro(null);
        outboxRepository.save(entrega);
    }

    @Transactional
    public void registrarFalhaTemporaria(Long outboxId, Throwable erro, Integer maxTentativas) {
        NotificacaoOutbox entrega = buscar(outboxId);
        int tentativas = entrega.getTentativas() + 1;
        entrega.setTentativas(tentativas);
        entrega.setUltimoErro(resumirErro(erro));
        entrega.setDtProcessandoDesde(null);

        if (tentativas >= maxTentativas) {
            entrega.setStatus(StatusEnvioNotificacao.FALHA);
        } else {
            entrega.setStatus(StatusEnvioNotificacao.PENDENTE);
            entrega.setDtProximaTentativa(LocalDateTime.now().plusSeconds(calcularEspera(tentativas)));
        }
        outboxRepository.save(entrega);
    }

    @Transactional
    public void registrarFalhaPermanente(Long outboxId, Throwable erro) {
        NotificacaoOutbox entrega = buscar(outboxId);
        entrega.setTentativas(entrega.getTentativas() + 1);
        entrega.setStatus(StatusEnvioNotificacao.FALHA);
        entrega.setUltimoErro(resumirErro(erro));
        entrega.setDtProcessandoDesde(null);
        outboxRepository.save(entrega);
    }

    private NotificacaoOutbox buscar(Long outboxId) {
        return outboxRepository.buscarDetalhadoPorId(outboxId).orElseThrow(() -> new ResourceNotFound("Entrega de notificação não encontrada."));
    }

    private long calcularEspera(int tentativas) {
        int expoente = Math.min(tentativas - 1, 20);
        long espera;
        try {
            espera = Math.multiplyExact(retryBaseSegundos, 1L << expoente);
        } catch (ArithmeticException exception) {
            espera = retryMaxSegundos;
        }
        return Math.min(espera, retryMaxSegundos);
    }

    private String resumirErro(Throwable erro) {
        String mensagem = erro.getMessage();
        if (mensagem == null || mensagem.isBlank()) {
            mensagem = erro.getClass().getSimpleName();
        }
        return mensagem.length() <= 1000 ? mensagem : mensagem.substring(0, 1000);
    }
}
