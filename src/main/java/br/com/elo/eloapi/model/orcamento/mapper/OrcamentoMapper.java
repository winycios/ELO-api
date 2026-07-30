package br.com.elo.eloapi.model.orcamento.mapper;

import br.com.elo.eloapi.model.areaAtendimento.AreaAtendimento;
import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.orcamento.Orcamento;
import br.com.elo.eloapi.model.orcamento.OrcamentoCusto;
import br.com.elo.eloapi.model.orcamento.OrcamentoEndereco;
import br.com.elo.eloapi.model.orcamento.OrcamentoImagem;
import br.com.elo.eloapi.model.orcamento.dto.*;
import br.com.elo.eloapi.model.orcamentoStatus.OrcamentoStatus;
import br.com.elo.eloapi.model.orcamentoStatus.TipoOrcamentoStatus;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

import static br.com.elo.eloapi.Util.Utils.calcularDistancia;

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

    public static OrcamentoEndereco toOrcamentoEnderecoEntity(Endereco endereco, Orcamento orcamento) {
        OrcamentoEndereco snapshot = new OrcamentoEndereco();
        snapshot.setOrcamento(orcamento);
        orcamento.setEndereco(snapshot);
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

    public static Endereco toEnderecoEntity(OrcamentoEndereco enderecoOrcamento) {
        Endereco endereco = new Endereco();

        endereco.setNmRua(enderecoOrcamento.getNmRua());
        endereco.setNrRua(enderecoOrcamento.getNrRua());
        endereco.setNmComplemento(enderecoOrcamento.getNmComplemento());
        endereco.setNmBairro(enderecoOrcamento.getNmBairro());
        endereco.setNmCidade(enderecoOrcamento.getNmCidade());
        endereco.setNmEstado(enderecoOrcamento.getNmEstado());
        endereco.setNrCep(enderecoOrcamento.getNrCep());
        endereco.setNrLatitude(enderecoOrcamento.getNrLatitude());
        endereco.setNrLongitude(enderecoOrcamento.getNrLongitude());
        return endereco;
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

    public static OrcamentoListagemRS toListagemResponse(Orcamento orcamento) {
        Servico servico = orcamento.getServico();
        Profissional profissional = servico.getProfissional();
        Usuario usuarioProfissional = profissional.getUsuario();
        TipoOrcamentoStatus status = orcamento.getOrcamentoStatus().getTipoOrcamentoStatus();
        String fotoProfissional = profissional.getUriPerfil() != null ? profissional.getUriPerfil() : usuarioProfissional.getUriPerfil();

        return new OrcamentoListagemRS(
                orcamento.getId(),
                servico.getId(),
                profissional.getId(),
                status.isOrcamentoFinal() ? orcamento.getId() : null,
                usuarioProfissional.nomeCompleto(),
                fotoProfissional,
                servico.getCategoriaEspecifica().getNmCategoria(),
                orcamento.getDsDescricao(),
                status.getDescricao()
        );
    }

    public static OrcamentoListagemProfissionalRS orcamentoListagemProfissionalResponse(Orcamento orcamento, AreaAtendimento areaAtendimento) {
        Servico servico = orcamento.getServico();
        Profissional profissional = servico.getProfissional();
        Usuario usuarioProfissional = profissional.getUsuario();
        TipoOrcamentoStatus status = orcamento.getOrcamentoStatus().getTipoOrcamentoStatus();
        String fotoProfissional = profissional.getUriPerfil() != null ? profissional.getUriPerfil() : usuarioProfissional.getUriPerfil();

        return new OrcamentoListagemProfissionalRS(
                orcamento.getId(),
                servico.getId(),
                usuarioProfissional.nomeCompleto(),
                fotoProfissional,
                0.0,
                servico.getCategoriaEspecifica().getNmCategoria(),
                orcamento.getDsDescricao(),
                calcularDistancia(orcamento.getUsuario(), areaAtendimento, toEnderecoEntity(orcamento.getEndereco())),
                orcamento.getDtCriacao(),
                status.getDescricao()
        );
    }

    public static OrcamentoDetalheRS toDetalheResponse(Orcamento orcamento, List<OrcamentoImagem> imagens, OrcamentoEndereco endereco, List<OrcamentoCusto> custos) {
        Servico servico = orcamento.getServico();
        Profissional profissional = servico.getProfissional();
        Usuario usuarioProfissional = profissional.getUsuario();
        TipoOrcamentoStatus status = orcamento.getOrcamentoStatus().getTipoOrcamentoStatus();
        String fotoProfissional = profissional.getUriPerfil() != null ? profissional.getUriPerfil() : usuarioProfissional.getUriPerfil();

        double valorCustos = custos.stream().map(OrcamentoCusto::getVl_valor).filter(java.util.Objects::nonNull).mapToDouble(Double::doubleValue).sum();

        Double valorExibicao = custos.isEmpty() ? servico.getVlServico() : valorCustos;

        OrcamentoDetalheRS.ProfissionalOrcamentoRS profissionalResponse =
                new OrcamentoDetalheRS.ProfissionalOrcamentoRS(
                        profissional.getId(),
                        usuarioProfissional.nomeCompleto(),
                        fotoProfissional,
                        servico.getCategoriaEspecifica().getNmCategoria(),
                        usuarioProfissional.getQtAvaliacaoGeral(),
                        usuarioProfissional.getQtAvalicaoes(),
                        Boolean.TRUE.equals(profissional.getStHabilitado())
                                && Boolean.TRUE.equals(usuarioProfissional.getStHabilitado()),
                        new OrcamentoDetalheRS.ContatoProfissionalRS(
                                usuarioProfissional.getTelCelular(),
                                usuarioProfissional.getTelWhats()
                        )
                );

        OrcamentoDetalheRS.SolicitacaoOrcamentoRS solicitacaoResponse =
                new OrcamentoDetalheRS.SolicitacaoOrcamentoRS(
                        servico.getId(),
                        servico.getCategoriaEspecifica().getId(),
                        servico.getCategoriaEspecifica().getNmCategoria(),
                        orcamento.getDsDescricao(),
                        servico.getTipoServico() == null
                                ? null
                                : servico.getTipoServico().getTipoServico(),
                        orcamento.getDtPreferidoSolicitado(),
                        valorExibicao,
                        imagens.stream().map(OrcamentoImagem::getUrl).toList(),
                        endereco == null ? null : toDetalheEnderecoResponse(endereco)
                );

        OrcamentoDetalheRS.OrcamentoFinalRS orcamentoFinalResponse = null;
        if (status.isOrcamentoFinal()) {
            orcamentoFinalResponse = new OrcamentoDetalheRS.OrcamentoFinalRS(
                    orcamento.getId(),
                    orcamento.getDtInicioProposto(),
                    orcamento.getDtFimProposto(),
                    orcamento.getDsObservacaoProfissional(),
                    custos.stream().map(custo -> new OrcamentoDetalheRS.CustoOrcamentoRS(custo.getId(), custo.getDsDescricao(), custo.getVl_valor())).toList(),
                    valorCustos
            );
        }

        return new OrcamentoDetalheRS(
                orcamento.getId(),
                status.getDescricao(),
                profissionalResponse,
                solicitacaoResponse,
                orcamentoFinalResponse
        );
    }

    private static OrcamentoDetalheRS.EnderecoOrcamentoRS toDetalheEnderecoResponse(OrcamentoEndereco endereco) {
        return new OrcamentoDetalheRS.EnderecoOrcamentoRS(
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
