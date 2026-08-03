package br.com.elo.eloapi.model.notificacao;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
public enum TipoNotificacao {
    ORCAMENTO_SOLICITADO(CanalNotificacao.PUSH, CanalNotificacao.EMAIL),
    ORCAMENTO_FINAL_CRIADO(CanalNotificacao.PUSH, CanalNotificacao.EMAIL),
    ORCAMENTO_APROVADO(CanalNotificacao.PUSH, CanalNotificacao.EMAIL),
    ORCAMENTO_RECUSADO(CanalNotificacao.PUSH, CanalNotificacao.EMAIL),
    ORCAMENTO_CANCELADO_CLIENTE(CanalNotificacao.PUSH, CanalNotificacao.EMAIL),
    ORCAMENTO_EXPIRADO(CanalNotificacao.PUSH),
    ORCAMENTO_CONCLUIDO(CanalNotificacao.PUSH);

    private final Set<CanalNotificacao> canais;

    TipoNotificacao(CanalNotificacao primeiro, CanalNotificacao... demais) {
        EnumSet<CanalNotificacao> configurados = EnumSet.of(primeiro, demais);
        this.canais = Set.copyOf(configurados);
    }

}
