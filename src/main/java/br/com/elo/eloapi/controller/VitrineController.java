package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.publicacao.dto.*;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.service.VitrineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/vitrine")
@RequiredArgsConstructor
public class VitrineController {
    private final VitrineService vitrineService;

    @GetMapping("/listar")
    public ResponseEntity<CursorPageRS<PublicacaoFeedRS>> listarFeed(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String cursor,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok().body(vitrineService.listarFeed(categoriaId, cursor, usuario, false));
    }

    @GetMapping("profissional/listar")
    public ResponseEntity<CursorPageRS<PublicacaoFeedRS>> listarFeedPorProfissional(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String cursor,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok().body(vitrineService.listarFeed(categoriaId, cursor, usuario, true));
    }

    @PostMapping("profissional/publicacao")
    public ResponseEntity<PublicacaoFeedRS> salvarPublicacao(@RequestBody PublicacaoCreateDTO publicacaoCreateDTO, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok().body(vitrineService.salvarPublicacao(publicacaoCreateDTO, usuario));
    }

    @DeleteMapping("profissional/publicacao/{id}")
    public ResponseEntity<Void> desativarPublicacao(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
        vitrineService.desativarPublicacao(usuario, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/publicacoes/{publicacaoId}/curtidas")
    public ResponseEntity<Void> curtir(
            @PathVariable Long publicacaoId,
            @AuthenticationPrincipal Usuario usuario) {
        vitrineService.curtir(publicacaoId, usuario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/publicacoes/{publicacaoId}/curtidas")
    public ResponseEntity<Void> descurtir(
            @PathVariable Long publicacaoId,
            @AuthenticationPrincipal Usuario usuario) {
        vitrineService.descurtir(publicacaoId, usuario);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/publicacoes/{publicacaoId}/comentarios")
    public ResponseEntity<ComentarioRS> comentar(
            @PathVariable Long publicacaoId,
            @RequestBody @Valid ComentarioCreateRQ request,
            @AuthenticationPrincipal Usuario usuario) {
        ComentarioRS response = vitrineService.comentar(publicacaoId, request, usuario);
        return ResponseEntity.created(URI.create("/api/vitrine/publicacoes/" + publicacaoId + "/comentarios/" + response.id())).body(response);
    }

    @GetMapping("/publicacoes/{publicacaoId}/comentarios")
    public ResponseEntity<CursorPageRS<ComentarioRS>> listarComentarios(
            @PathVariable Long publicacaoId,
            @RequestParam(required = false) String cursor) {
        return ResponseEntity.ok().body(vitrineService.listarComentarios(publicacaoId, cursor));
    }
}
