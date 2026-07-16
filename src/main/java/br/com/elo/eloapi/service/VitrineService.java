package br.com.elo.eloapi.service;

import br.com.elo.eloapi.exception.BadRequestException;
import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.publicacao.Publicacao;
import br.com.elo.eloapi.model.publicacao.PublicacaoComentario;
import br.com.elo.eloapi.model.publicacao.PublicacaoCurtida;
import br.com.elo.eloapi.model.publicacao.PublicacaoCurtidaId;
import br.com.elo.eloapi.model.publicacao.dto.*;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VitrineService {
    public static final int PAGE_SIZE = 20;

    private final PublicacaoRepository publicacaoRepository;
    private final PublicacaoImagemRepository imagemRepository;
    private final PublicacaoCurtidaRepository curtidaRepository;
    private final PublicacaoComentarioRepository comentarioRepository;
    private final CursorCodec cursorCodec;

    @Transactional(readOnly = true)
    public CursorPageRS<PublicacaoFeedRS> listarFeed(Long categoriaId, String cursor, Usuario usuario) {
        CursorCodec.CursorValue cursorValue = cursorCodec.decode(cursor);
        List<Publicacao> encontrados = publicacaoRepository.buscarFeed(categoriaId, cursorValue.data(), cursorValue.id(), PageRequest.of(0, PAGE_SIZE + 1));

        boolean hasNext = encontrados.size() > PAGE_SIZE;
        List<Publicacao> pagina = encontrados.stream().limit(PAGE_SIZE).toList();
        if (pagina.isEmpty()) {
            return new CursorPageRS<>(List.of(), null, false);
        }

        List<Long> ids = pagina.stream().map(Publicacao::getId).toList();
        Map<Long, List<PublicacaoImagemRS>> imagens = imagemRepository
                .findByPublicacaoIdInOrderByPublicacaoIdAscOrdemAsc(ids).stream()
                .collect(Collectors.groupingBy(
                        imagem -> imagem.getPublicacao().getId(),
                        Collectors.mapping(imagem -> new PublicacaoImagemRS(
                                imagem.getId(), imagem.getUrl(), imagem.getOrdem()), Collectors.toList())));

        Map<Long, br.com.elo.eloapi.model.publicacao.dto.PublicacaoCurtida> curtidas;
        if (usuario != null) {
            curtidas = curtidaRepository.contarPorPublicacoesECurtida(ids, usuario.getId()).stream()
                    .collect(Collectors.toMap(PublicacaoCurtidaRepository.Contagem::getPublicacaoId,
                            curtida -> new br.com.elo.eloapi.model.publicacao.dto.PublicacaoCurtida(curtida.getPublicacaoId(), curtida.getTotal(), curtida.getCurtida())));
        } else {
            curtidas = curtidaRepository.contarPorPublicacoes(ids).stream()
                    .collect(Collectors.toMap(PublicacaoCurtidaRepository.Contagem::getPublicacaoId,
                            curtida -> new br.com.elo.eloapi.model.publicacao.dto.PublicacaoCurtida(curtida.getPublicacaoId(), curtida.getTotal(), false)));
        }

        Map<Long, Long> comentarios = comentarioRepository.contarAtivosPorPublicacoes(ids).stream()
                .collect(Collectors.toMap(PublicacaoComentarioRepository.Contagem::getPublicacaoId,
                        PublicacaoComentarioRepository.Contagem::getTotal));

        List<PublicacaoFeedRS> items = pagina.stream()
                .map(publicacao -> toFeedResponse(publicacao, imagens, curtidas, comentarios))
                .toList();
        Publicacao ultimo = pagina.getLast();
        String nextCursor = hasNext ? cursorCodec.encode(ultimo.getDtPublicacao(), ultimo.getId()) : null;
        return new CursorPageRS<>(items, nextCursor, hasNext);
    }

    @Transactional
    public void curtir(Long publicacaoId, Usuario usuario) {
        PublicacaoCurtidaId id = new PublicacaoCurtidaId(publicacaoId, usuario.getId());
        if (curtidaRepository.existsById(id)) {
            return;
        }
        Publicacao publicacao = buscarPublicacaoAtiva(publicacaoId);
        curtidaRepository.save(new PublicacaoCurtida(publicacao, usuario));
    }

    @Transactional
    public void descurtir(Long publicacaoId, Usuario usuario) {
        curtidaRepository.deleteById(new PublicacaoCurtidaId(publicacaoId, usuario.getId()));
    }

    @Transactional
    public ComentarioRS comentar(Long publicacaoId, ComentarioCreateRQ request, Usuario usuario) {
        Publicacao publicacao = buscarPublicacaoAtiva(publicacaoId);
        PublicacaoComentario pai = null;
        if (request.comentarioPaiId() != null) {
            pai = comentarioRepository.findById(request.comentarioPaiId())
                    .filter(comentario -> comentario.getPublicacao().getId().equals(publicacaoId))
                    .filter(comentario -> Boolean.TRUE.equals(comentario.getAtivo()))
                    .orElseThrow(() -> new BadRequestException("Comentário inválido para esta publicação."));
        }

        PublicacaoComentario comentario = comentarioRepository.save(
                new PublicacaoComentario(publicacao, usuario, pai, request.texto().trim()));
        return toComentarioResponse(comentario);
    }

    @Transactional(readOnly = true)
    public CursorPageRS<ComentarioRS> listarComentarios(Long publicacaoId, String cursor) {
        if (!publicacaoRepository.existsById(publicacaoId)) {
            throw new ResourceNotFound("Publicação não encontrada.");
        }
        CursorCodec.CursorValue cursorValue = cursorCodec.decode(cursor);
        List<PublicacaoComentario> encontrados = comentarioRepository.buscarPagina(
                publicacaoId, cursorValue.data(), cursorValue.id(), PageRequest.of(0, PAGE_SIZE + 1));
        boolean hasNext = encontrados.size() > PAGE_SIZE;
        List<PublicacaoComentario> pagina = encontrados.stream().limit(PAGE_SIZE).toList();
        List<ComentarioRS> items = pagina.stream().map(this::toComentarioResponse).toList();
        String nextCursor = null;
        if (hasNext) {
            PublicacaoComentario ultimo = pagina.get(pagina.size() - 1);
            nextCursor = cursorCodec.encode(ultimo.getDataComentario(), ultimo.getId());
        }
        return new CursorPageRS<>(items, nextCursor, hasNext);
    }

    private Publicacao buscarPublicacaoAtiva(Long id) {
        return publicacaoRepository.findById(id)
                .filter(publicacao -> Boolean.TRUE.equals(publicacao.getStAtivo()))
                .orElseThrow(() -> new ResourceNotFound("Publicação não encontrada."));
    }

    private PublicacaoFeedRS toFeedResponse(
            Publicacao p,
            Map<Long, List<PublicacaoImagemRS>> imagens,
            Map<Long, br.com.elo.eloapi.model.publicacao.dto.PublicacaoCurtida> curtidas,
            Map<Long, Long> comentarios) {
        Usuario usuario = p.getProfissional().getUsuario();
        return new PublicacaoFeedRS(
                p.getId(), p.getDsPublicacao(), p.getDtPublicacao(),
                p.getCategoriaEspecifica().getId(), p.getCategoriaEspecifica().getNmCategoria(),
                p.getProfissional().getId(), usuario.nomeCompleto(), usuario.getUriPerfil(),
                imagens.getOrDefault(p.getId(), List.of()),
                curtidas.getOrDefault(p.getId(), new br.com.elo.eloapi.model.publicacao.dto.PublicacaoCurtida(0L, 0L, false)).totalCurtida(),
                comentarios.getOrDefault(p.getId(), 0L),
                curtidas.getOrDefault(p.getId(), new br.com.elo.eloapi.model.publicacao.dto.PublicacaoCurtida(0L, 0L, false)).isCurtido()
        );
    }

    private ComentarioRS toComentarioResponse(PublicacaoComentario comentario) {
        Usuario usuario = comentario.getUsuario();
        return new ComentarioRS(
                comentario.getId(), comentario.getTexto(), comentario.getDataComentario(),
                comentario.getComentarioPai() == null ? null : comentario.getComentarioPai().getId(),
                usuario.getId(), usuario.nomeCompleto(), usuario.getUriPerfil());
    }
}
