package br.com.elo.eloapi.service;

import br.com.elo.eloapi.exception.BadRequestException;
import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.publicacao.*;
import br.com.elo.eloapi.model.publicacao.PublicacaoCurtida;
import br.com.elo.eloapi.model.publicacao.dto.*;
import br.com.elo.eloapi.model.publicacao.mapper.PublicacaoMapper;
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
    private final CategoriaEspecificaRepository categoriaEspecificaRepository;
    private final CursorCodec cursorCodec;
    private final ProfissionalRepository profissionalRepository;

    @Transactional(readOnly = true)
    public CursorPageRS<PublicacaoFeedRS> listarFeed(Long categoriaId, String cursor, Usuario usuario, Boolean isProfissional) {
        CursorCodec.CursorValue cursorValue = cursorCodec.decode(cursor);
        Long idUsuario = usuario == null ? null : usuario.getId();
        List<Publicacao> encontrados = publicacaoRepository.buscarFeed(
                categoriaId, idUsuario, isProfissional, cursorValue.data(), cursorValue.id(),
                PageRequest.of(0, PAGE_SIZE + 1));

        boolean hasNext = encontrados.size() > PAGE_SIZE;
        List<Publicacao> pagina = encontrados.stream().limit(PAGE_SIZE).toList();
        if (pagina.isEmpty()) {
            return new CursorPageRS<>(List.of(), null, false);
        }

        List<Long> ids = pagina.stream().map(Publicacao::getId).toList();
        Map<Long, List<PublicacaoImagemRS>> imagens = imagemRepository
                .findByPublicacaoIdInOrderByPublicacaoIdAscOrdemAsc(ids).stream()
                .collect(Collectors.groupingBy(
                        imagem -> imagem.getPublicacao().getId(), Collectors.mapping(PublicacaoMapper::toImageResponse, Collectors.toList())));

        Map<Long, br.com.elo.eloapi.model.publicacao.dto.PublicacaoCurtida> curtidas;
        if (usuario != null) {
            curtidas = curtidaRepository.contarPorPublicacoesECurtida(ids, usuario.getId()).stream().collect(Collectors.toMap(PublicacaoCurtidaRepository.Contagem::getPublicacaoId, curtida -> new br.com.elo.eloapi.model.publicacao.dto.PublicacaoCurtida(curtida.getPublicacaoId(), curtida.getTotal(), curtida.getCurtida())));
        } else {
            curtidas = curtidaRepository.contarPorPublicacoes(ids).stream().collect(Collectors.toMap(PublicacaoCurtidaRepository.Contagem::getPublicacaoId, curtida -> new br.com.elo.eloapi.model.publicacao.dto.PublicacaoCurtida(curtida.getPublicacaoId(), curtida.getTotal(), false)));
        }

        Map<Long, Long> comentarios = comentarioRepository.contarAtivosPorPublicacoes(ids).stream().collect(Collectors.toMap(PublicacaoComentarioRepository.Contagem::getPublicacaoId, PublicacaoComentarioRepository.Contagem::getTotal));

        List<PublicacaoFeedRS> items = pagina.stream().map(publicacao -> PublicacaoMapper.toFeedResponse(publicacao, imagens, curtidas, comentarios)).toList();
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

        PublicacaoComentario comentario = comentarioRepository.save(new PublicacaoComentario(publicacao, usuario, pai, request.texto().trim()));
        return PublicacaoMapper.toCommentResponse(comentario);
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
        List<ComentarioRS> items = pagina.stream().map(PublicacaoMapper::toCommentResponse).toList();
        String nextCursor = null;
        if (hasNext) {
            PublicacaoComentario ultimo = pagina.get(pagina.size() - 1);
            nextCursor = cursorCodec.encode(ultimo.getDataComentario(), ultimo.getId());
        }
        return new CursorPageRS<>(items, nextCursor, hasNext);
    }

    @Transactional
    public PublicacaoFeedRS salvarPublicacao(PublicacaoCreateDTO publicacaoCreateDTO, Usuario usuario) {
        Publicacao publicacao = PublicacaoMapper.toEntity(publicacaoCreateDTO);

        Profissional profissional = profissionalRepository.findById(usuario.getId()).orElseThrow(() -> new ResourceNotFound("Profissional não encontrado"));

        publicacao.setProfissional(profissional);
        publicacao.setCategoriaEspecifica(categoriaEspecificaRepository.findById(publicacaoCreateDTO.getIdCategoriaEspecifica()).orElseThrow(() -> new ResourceNotFound("Categoria específica não encontrada")));
        publicacao = publicacaoRepository.save(publicacao);

        Publicacao finalPublicacao = publicacao;
        List<PublicacaoImagem> imagens = imagemRepository.saveAll(publicacaoCreateDTO.getPublicacaoImagemDTOList().stream().map(image -> PublicacaoMapper.toImageEntity(image, finalPublicacao)).toList());

        Map<Long, List<PublicacaoImagemRS>> imagensPorPublicacao = Map.of(publicacao.getId(), imagens.stream().map(PublicacaoMapper::toImageResponse).toList());
        return PublicacaoMapper.toFeedResponse(publicacao, imagensPorPublicacao, Map.of(), Map.of());
    }

    @Transactional
    public void desativarPublicacao(Usuario usuario, Long publlicacaoId) {
        Profissional profissional = profissionalRepository.findById(usuario.getId()).orElseThrow(() -> new ResourceNotFound("Profissional não encontrado"));
        Publicacao publicacao = publicacaoRepository.findByIdAndProfissionalIdAndStAtivoTrue(publlicacaoId, profissional.getId()).orElseThrow(() -> new ResourceNotFound("Publicacao ativo não encontrado para este profissional"));
        publicacao.setStAtivo(false);
        publicacaoRepository.save(publicacao);
    }

    private Publicacao buscarPublicacaoAtiva(Long id) {
        return publicacaoRepository.findById(id).filter(publicacao -> Boolean.TRUE.equals(publicacao.getStAtivo())).orElseThrow(() -> new ResourceNotFound("Publicação não encontrada."));
    }

}
