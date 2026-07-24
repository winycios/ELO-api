package br.com.elo.eloapi.model.orcamento.mapper;

import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.orcamento.Orcamento;
import br.com.elo.eloapi.model.orcamento.OrcamentoEndereco;
import br.com.elo.eloapi.model.orcamento.OrcamentoImagem;
import br.com.elo.eloapi.model.orcamento.dto.OrcamentoCreateRQ;
import br.com.elo.eloapi.model.orcamento.dto.OrcamentoRS;
import br.com.elo.eloapi.model.orcamentoStatus.OrcamentoStatus;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class OrcamentoMapper {

    public OrcamentoMapper() {
    }

    public static Orcamento toEntity(
            OrcamentoCreateRQ dto,
            Servico servico,
            Usuario cliente,
            OrcamentoStatus status
    ) {
        Orcamento orcamento = new Orcamento();
        orcamento.setServico(servico);
        orcamento.setUsuario(cliente);
        orcamento.setOrcamentoStatus(status);
        orcamento.setDsDescricao(dto.descricao().trim());
        orcamento.setDtPreferidoSolicitado(dto.dtPreferidoSolicitado());
        return orcamento;
    }

    public static OrcamentoImagem toImagemEntity(String url, Orcamento orcamento) {
        return new OrcamentoImagem(null, orcamento, url.trim());
    }

    public static OrcamentoEndereco toEnderecoEntity(Endereco endereco, Orcamento orcamento) {
        OrcamentoEndereco snapshot = new OrcamentoEndereco();
        snapshot.setOrcamento(orcamento);
        snapshot.setNmRua(endereco.getNmRua());
        snapshot.setNrRua(endereco.getNrRua());
        snapshot.setNmComplemento(endereco.getNmComplemento());
        snapshot.setNmBairro(endereco.getNmBairro());
        snapshot.setNmCidade(endereco.getNmCidade());
        snapshot.setNmEstado(endereco.getNmEstado());
        snapshot.setNrCep(endereco.getNrCep());
        snapshot.setNrLatitude(endereco.getNrLatitude());
        snapshot.setNrLongitude(endereco.getNrLongitude());
        return snapshot;
    }

    public static OrcamentoRS toResponse(
            Orcamento orcamento,
            List<OrcamentoImagem> imagens,
            OrcamentoEndereco endereco
    ) {
        return new OrcamentoRS(
                orcamento.getId(),
                orcamento.getServico().getId(),
                orcamento.getServico().getProfissional().getId(),
                orcamento.getUsuario().getId(),
                orcamento.getOrcamentoStatus().getTipoOrcamentoStatus().getDescricao(),
                orcamento.getDsDescricao(),
                orcamento.getDtPreferidoSolicitado(),
                orcamento.getDtInicioProposto(),
                orcamento.getDtFimProposto(),
                imagens.stream().map(OrcamentoImagem::getUrl).toList(),
                endereco == null ? null : toEnderecoResponse(endereco)
        );
    }

    private static OrcamentoRS.EnderecoOrcamentoRS toEnderecoResponse(OrcamentoEndereco endereco) {
        return new OrcamentoRS.EnderecoOrcamentoRS(
                endereco.getNmRua(),
                endereco.getNrRua(),
                endereco.getNmComplemento(),
                endereco.getNmBairro(),
                endereco.getNmCidade(),
                endereco.getNmEstado(),
                endereco.getNrCep(),
                endereco.getNrLatitude(),
                endereco.getNrLongitude()
        );
    }
}
