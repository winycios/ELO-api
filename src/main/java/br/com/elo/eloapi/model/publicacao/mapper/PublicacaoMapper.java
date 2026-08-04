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
import java.util.function.UnaryOperator;

@Component
@AllArgsConstructor
public final class PublicacaoMapper {

    public static Publicacao toEntity(PublicacaoCreateRQ dto) {
        Publicacao publicacao = new Publicacao();
        publicacao.setDsPublicacao(dto.dsPublicacao());
        publicacao.setDtPublicacao(LocalDateTime.now());
        publicacao.setStAtivo(true);
        return publicacao;
    }

    public static PublicacaoImagem toImageEntity(PublicacaoImagemRQ dto, Publicacao publicacao) {
        return new PublicacaoImagem(null, publicacao, dto.chaveImagem(), dto.nrOrdem());
    }

    public static PublicacaoImagemRS toImageResponse(PublicacaoImagem imagem, UnaryOperator<String> resolverImagem) {
        return new PublicacaoImagemRS(imagem.getId(), resolverImagem.apply(imagem.getChave()), imagem.getOrdem());
    }

    public static PublicacaoFeedRS toFeedResponse(
            Publicacao publicacao,
            Map<Long, List<PublicacaoImagemRS>> imagens,
            Map<Long, PublicacaoCurtida> curtidas,
            Map<Long, Long> comentarios,
            UnaryOperator<String> resolverImagemPerfil) {
        Usuario usuario = publicacao.getProfissional().getUsuario();
        String fotoPerfil = publicacao.getProfissional().getUriPerfil() != null
                ? publicacao.getProfissional().getUriPerfil()
                : usuario.getUriPerfil();
        PublicacaoCurtida curtida = curtidas.getOrDefault(
                publicacao.getId(), new PublicacaoCurtida(publicacao.getId(), 0L, false));

        return new PublicacaoFeedRS(
                publicacao.getId(), publicacao.getDsPublicacao(), publicacao.getDtPublicacao(),
                publicacao.getCategoriaEspecifica().getId(), publicacao.getCategoriaEspecifica().getNmCategoria(),
                publicacao.getProfissional().getId(), usuario.nomeCompleto(), resolverImagemPerfil.apply(fotoPerfil),
                imagens.getOrDefault(publicacao.getId(), List.of()),
                curtida.totalCurtida(),
                comentarios.getOrDefault(publicacao.getId(), 0L),
                curtida.isCurtido()
        );
    }

    public static ComentarioRS toCommentResponse(PublicacaoComentario comentario, UnaryOperator<String> resolverImagemPerfil) {
        Usuario usuario = comentario.getUsuario();
        return new ComentarioRS(
                comentario.getId(), comentario.getTexto(), comentario.getDataComentario(),
                comentario.getComentarioPai() == null ? null : comentario.getComentarioPai().getId(),
                usuario.getId(), usuario.nomeCompleto(), resolverImagemPerfil.apply(usuario.getUriPerfil()));
    }
}
