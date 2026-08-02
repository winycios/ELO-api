package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.servico.ProfissionalServicoRS;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.service.ProfissionalDetalhesService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/estimativa")
@RequiredArgsConstructor
public class ProfissionalDetalhesController {

    private final ProfissionalDetalhesService profissionalDetalhesService;

    @GetMapping(value = "profissional/{id}/detalhes", params = "servicoId")
    public ResponseEntity<ProfissionalServicoRS> buscarDetalhes(@PathVariable @Positive Long id, @RequestParam @Positive Long servicoId, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(profissionalDetalhesService.buscarDetalhes(id, servicoId, usuario, null));
    }

    @GetMapping(value = "profissional/{id}/detalhes", params = "categoriaId")
    public ResponseEntity<ProfissionalServicoRS> buscarDetalhesPorCategoria(@PathVariable @Positive Long id, @RequestParam @Positive Long categoriaId, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(profissionalDetalhesService.buscarDetalhesPorCategoria(id, categoriaId, usuario));
    }

    @GetMapping(value = "profissional/{id}/detalhes/comentarios", params = "categoriaId")
    public ResponseEntity<List<ProfissionalServicoRS.AvaliacaoRS>> buscarDetalhesComentarios(@PathVariable @Positive Long id, @RequestParam @Positive Long categoriaId) {
        return ResponseEntity.ok(profissionalDetalhesService.buscarDetalhesComentarios(id, categoriaId));
    }
}
