package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.servico.dto.ServicoCreateDTO;
import br.com.elo.eloapi.model.servico.dto.ServicoListaRS;
import br.com.elo.eloapi.model.servico.dto.ServicoRS;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.service.ProfissionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissional")
@RequiredArgsConstructor
public class ProfissionalController {

    private final ProfissionalService profissionalService;

    @PostMapping("/servico")
    public ResponseEntity<ServicoRS> salvarServico(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid ServicoCreateDTO servicoCreateDTO) {
        return ResponseEntity.ok(profissionalService.salvarServico(usuario, servicoCreateDTO));
    }

    @GetMapping("/servico/listar")
    public ResponseEntity<List<ServicoListaRS>> listarServicos(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(profissionalService.listarServicos(usuario));
    }

    @GetMapping("/servico/{id}")
    public ResponseEntity<ServicoRS> buscarServico(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
        return ResponseEntity.ok(profissionalService.buscarServico(usuario, id));
    }

    @DeleteMapping("/servico/{id}")
    public ResponseEntity<Void> desativarServico(@AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
        profissionalService.desativarServico(usuario, id);
        return ResponseEntity.noContent().build();
    }
}
