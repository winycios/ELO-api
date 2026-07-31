package br.com.elo.eloapi.model.orcamento.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrcamentoDetalheRS(
        Long id,
        String status,
        ProfissionalOrcamentoRS profissional,
        SolicitacaoOrcamentoRS solicitacao,
        OrcamentoFinalRS orcamentoFinal,
        CancelamentoRS cancelamento,
        ConclusaoRS conclusao
) {

    public record ProfissionalOrcamentoRS(
            Long id,
            String nome,
            String fotoPerfil,
            String categoria,
            Double avaliacao,
            Integer quantidadeAvaliacoes,
            Boolean verificado,
            ContatoProfissionalRS contato
    ) {
    }

    public record ContatoProfissionalRS(
            String telefone,
            String whatsapp
    ) {
    }

    public record SolicitacaoOrcamentoRS(
            Long idServico,
            Long idCategoria,
            String categoria,
            String descricao,
            String tipoServico,
            LocalDateTime horarioPreferido,
            Double valor,
            List<String> imagens,
            EnderecoOrcamentoRS endereco
    ) {
    }

    public record OrcamentoFinalRS(
            Long id,
            LocalDateTime inicioProposto,
            LocalDateTime fimProposto,
            String observacaoProfissional,
            List<CustoOrcamentoRS> custos,
            Double valorTotal
    ) {
    }

    public record CustoOrcamentoRS(
            Long id,
            String descricao,
            Double valor
    ) {
    }

    public record CancelamentoRS(
            String autor,
            Long idUsuario,
            String motivo,
            String descricao,
            LocalDateTime data
    ) {
    }

    public record ConclusaoRS(
            Long idProfissional,
            String observacao,
            LocalDateTime data
    ) {
    }

    public record EnderecoOrcamentoRS(
            String rua,
            Integer numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep,
            Double latitude,
            Double longitude
    ) {
    }
}
