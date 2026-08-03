package br.com.elo.eloapi.service.notificacao;

import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.notificacao.*;
import br.com.elo.eloapi.model.notificacao.dto.NotificacaoRS;
import br.com.elo.eloapi.model.orcamento.Orcamento;
import br.com.elo.eloapi.model.publicacao.dto.CursorPageRS;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.repository.DispositivoUsuarioRepository;
import br.com.elo.eloapi.repository.NotificacaoOutboxRepository;
import br.com.elo.eloapi.repository.NotificacaoRepository;
import br.com.elo.eloapi.service.CursorCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final NotificacaoOutboxRepository outboxRepository;
    private final DispositivoUsuarioRepository dispositivoRepository;
    private final CursorCodec cursorCodec;

    @Value("${elo.notification.push.enabled:false}")
    private boolean pushHabilitado;

    @Value("${elo.notification.email.enabled:false}")
    private boolean emailHabilitado;

    @Transactional(propagation = Propagation.MANDATORY)
    public void criar(TipoNotificacao tipo, Orcamento orcamento, Usuario destinatario, String titulo, String mensagem) {
        String chaveEvento = tipo.name() + ":" + orcamento.getId() + ":" + destinatario.getId();
        notificacaoRepository.findByChaveEvento(chaveEvento).orElseGet(() -> persistir(tipo, orcamento, destinatario, titulo, mensagem, chaveEvento));
    }

    private Notificacao persistir(TipoNotificacao tipo, Orcamento orcamento, Usuario destinatario, String titulo, String mensagem, String chaveEvento) {
        Notificacao notificacao = new Notificacao();
        notificacao.setTipo(tipo);
        notificacao.setOrcamento(orcamento);
        notificacao.setDestinatario(destinatario);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setChaveEvento(chaveEvento);
        notificacao = notificacaoRepository.save(notificacao);

        List<NotificacaoOutbox> entregas = new ArrayList<>();
        if (pushHabilitado && tipo.getCanais().contains(CanalNotificacao.PUSH)) {
            for (DispositivoUsuario dispositivo : dispositivoRepository.findAllByUsuarioIdAndAtivoTrue(destinatario.getId())) {
                entregas.add(novaEntrega(notificacao, CanalNotificacao.PUSH, dispositivo, notificacao.getId() + ":PUSH:" + dispositivo.getId()));
            }
        }
        if (emailHabilitado && tipo.getCanais().contains(CanalNotificacao.EMAIL)) {
            entregas.add(novaEntrega(notificacao, CanalNotificacao.EMAIL, null, notificacao.getId() + ":EMAIL:" + destinatario.getId()));
        }
        outboxRepository.saveAll(entregas);
        return notificacao;
    }

    private NotificacaoOutbox novaEntrega(Notificacao notificacao, CanalNotificacao canal, DispositivoUsuario dispositivo, String chaveIdempotencia) {
        NotificacaoOutbox entrega = new NotificacaoOutbox();
        entrega.setNotificacao(notificacao);
        entrega.setCanal(canal);
        entrega.setDispositivo(dispositivo);
        entrega.setChaveIdempotencia(chaveIdempotencia);
        return entrega;
    }

    @Transactional(readOnly = true)
    public CursorPageRS<NotificacaoRS> listar(Usuario usuario, String cursor, int tamanho) {
        Long cursorId = cursorCodec.decodeId(cursor);
        List<Notificacao> encontradas = notificacaoRepository.listarPorDestinatario(usuario.getId(), cursorId, PageRequest.of(0, tamanho + 1));
        boolean hasNext = encontradas.size() > tamanho;
        List<Notificacao> pagina = encontradas.stream().limit(tamanho).toList();
        String nextCursor = hasNext ? cursorCodec.encodeId(pagina.getLast().getId()) : null;
        return new CursorPageRS<>(pagina.stream().map(NotificacaoRS::from).toList(), nextCursor, hasNext);
    }

    @Transactional
    public NotificacaoRS marcarComoLida(Usuario usuario, Long notificacaoId) {
        Notificacao notificacao = notificacaoRepository.findByIdAndDestinatarioId(notificacaoId, usuario.getId()).orElseThrow(() -> new ResourceNotFound("Notificação não encontrada."));
        if (notificacao.getDtLeitura() == null) {
            notificacao.setDtLeitura(LocalDateTime.now());
            notificacaoRepository.save(notificacao);
        }
        return NotificacaoRS.from(notificacao);
    }

    @Transactional
    public void marcarTodasComoLidas(Usuario usuario) {
        notificacaoRepository.marcarTodasComoLidas(usuario.getId(), LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Usuario usuario) {
        return notificacaoRepository.countByDestinatarioIdAndDtLeituraIsNull(usuario.getId());
    }
}
