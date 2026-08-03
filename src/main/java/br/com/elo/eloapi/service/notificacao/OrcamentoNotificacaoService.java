package br.com.elo.eloapi.service.notificacao;

import br.com.elo.eloapi.model.notificacao.TipoNotificacao;
import br.com.elo.eloapi.model.orcamento.Orcamento;
import br.com.elo.eloapi.model.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrcamentoNotificacaoService {

    private final NotificacaoService notificacaoService;

    public void notificarSolicitacao(Orcamento orcamento) {
        Usuario destinatario = profissional(orcamento);
        notificacaoService.criar(
                TipoNotificacao.ORCAMENTO_SOLICITADO,
                orcamento,
                destinatario,
                "Nova solicitação de orçamento",
                cliente(orcamento).nomeCompleto() + " solicitou um orçamento."
        );
    }

    public void notificarOrcamentoFinal(Orcamento orcamento) {
        notificacaoService.criar(
                TipoNotificacao.ORCAMENTO_FINAL_CRIADO,
                orcamento,
                cliente(orcamento),
                "Você recebeu um orçamento",
                profissional(orcamento).nomeCompleto() + " enviou o orçamento final."
        );
    }

    public void notificarRecusa(Orcamento orcamento) {
        notificacaoService.criar(
                TipoNotificacao.ORCAMENTO_RECUSADO,
                orcamento,
                cliente(orcamento),
                "Solicitação de orçamento recusada",
                profissional(orcamento).nomeCompleto() + " recusou a solicitação de orçamento."
        );
    }

    public void notificarCancelamentoCliente(Orcamento orcamento) {
        notificacaoService.criar(
                TipoNotificacao.ORCAMENTO_CANCELADO_CLIENTE,
                orcamento,
                profissional(orcamento),
                "Orçamento cancelado pelo cliente",
                cliente(orcamento).nomeCompleto() + " cancelou o orçamento."
        );
    }

    public void notificarAprovacao(Orcamento orcamento) {
        notificacaoService.criar(
                TipoNotificacao.ORCAMENTO_APROVADO,
                orcamento,
                profissional(orcamento),
                "Orçamento aprovado",
                cliente(orcamento).nomeCompleto() + " aprovou o orçamento."
        );
    }

    public void notificarConclusao(Orcamento orcamento) {
        notificacaoService.criar(
                TipoNotificacao.ORCAMENTO_CONCLUIDO,
                orcamento,
                cliente(orcamento),
                "Serviço concluído",
                "O serviço do orçamento #" + orcamento.getId() + " foi marcado como concluído."
        );
    }

    public void notificarExpiracao(Orcamento orcamento) {
        String mensagem = "O orçamento #" + orcamento.getId() + " expirou.";
        notificacaoService.criar(
                TipoNotificacao.ORCAMENTO_EXPIRADO,
                orcamento,
                cliente(orcamento),
                "Orçamento expirado",
                mensagem
        );
        notificacaoService.criar(
                TipoNotificacao.ORCAMENTO_EXPIRADO,
                orcamento,
                profissional(orcamento),
                "Orçamento expirado",
                mensagem
        );
    }

    private Usuario cliente(Orcamento orcamento) {
        return orcamento.getUsuario();
    }

    private Usuario profissional(Orcamento orcamento) {
        return orcamento.getServico().getProfissional().getUsuario();
    }
}
