package br.com.elo.eloapi.model.publicacao.mapper;

import br.com.elo.eloapi.model.publicacao.Publicacao;
import br.com.elo.eloapi.model.publicacao.PublicacaoComentario;
import br.com.elo.eloapi.model.publicacao.PublicacaoImagem;
import br.com.elo.eloapi.model.publicacao.dto.*;
import br.com.elo.eloapi.model.usuario.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public final class PublicacaoMapper {

    public static Publicacao toEntity(PublicacaoCreateDTO dto) {
        Publicacao publicacao = new Publicacao();
        publicacao.setDsPublicacao(dto.getDsPublicacao());
        publicacao.setDtPublicacao(LocalDateTime.now());
        publicacao.setStAtivo(true);
        return publicacao;
    }

    public static PublicacaoImagem toImageEntity(PublicacaoImagemDTO dto, Publicacao publicacao) {
        return new PublicacaoImagem(null, publicacao, dto.getUrlImagem(), dto.getNrOrdem());
    }

    public static PublicacaoImagemRS toImageResponse(PublicacaoImagem imagem) {
        return new PublicacaoImagemRS(imagem.getId(), imagem.getUrl(), imagem.getOrdem());
    }

    public static PublicacaoFeedRS toFeedResponse(
            Publicacao publicacao,
            Map<Long, List<PublicacaoImagemRS>> imagens,
            Map<Long, PublicacaoCurtida> curtidas,
            Map<Long, Long> comentarios) {
        Usuario usuario = publicacao.getProfissional().getUsuario();
        PublicacaoCurtida curtida = curtidas.getOrDefault(
                publicacao.getId(), new PublicacaoCurtida(publicacao.getId(), 0L, false));

        return new PublicacaoFeedRS(
                publicacao.getId(), publicacao.getDsPublicacao(), publicacao.getDtPublicacao(),
                publicacao.getCategoriaEspecifica().getId(), publicacao.getCategoriaEspecifica().getNmCategoria(),
                publicacao.getProfissional().getId(), usuario.nomeCompleto(), usuario.getUriPerfil(),
                imagens.getOrDefault(publicacao.getId(), List.of()),
                curtida.totalCurtida(),
                comentarios.getOrDefault(publicacao.getId(), 0L),
                curtida.isCurtido()
        );
    }

    public static ComentarioRS toCommentResponse(PublicacaoComentario comentario) {
        Usuario usuario = comentario.getUsuario();
        return new ComentarioRS(
                comentario.getId(), comentario.getTexto(), comentario.getDataComentario(),
                comentario.getComentarioPai() == null ? null : comentario.getComentarioPai().getId(),
                usuario.getId(), usuario.nomeCompleto(), usuario.getUriPerfil());
    }
}
