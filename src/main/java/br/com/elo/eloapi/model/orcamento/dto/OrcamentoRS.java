package br.com.elo.eloapi.model.orcamento.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrcamentoRS(
        Long id,
        Long idServico,
        Long idProfissional,
        Long idCliente,
        String status,
        String descricao,
        LocalDateTime horarioPreferido,
        LocalDateTime inicioProposto,
        LocalDateTime fimProposto,
        List<String> imagens,
        EnderecoOrcamentoRS endereco
) {

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
