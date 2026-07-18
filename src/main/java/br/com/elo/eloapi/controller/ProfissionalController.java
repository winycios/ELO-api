package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.profissional.dto.ProfissionalRS;
import br.com.elo.eloapi.model.profissional.dto.ProfissionalUpdateDTO;
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

    @PutMapping("/perfil")
    public ResponseEntity<ProfissionalRS> salvarProfissional(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid ProfissionalUpdateDTO profissionalUpdateDTO) {
        return ResponseEntity.ok(profissionalService.salvarProfissional(usuario, profissionalUpdateDTO));
    }

    @GetMapping("/perfil")
    public ResponseEntity<ProfissionalRS> buscarProfissional(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(profissionalService.buscarProfissionalSessao(usuario));
    }

    @PatchMapping("/disponivel/{isAtivar}")
    public ResponseEntity<Void> DisponibilizaProfissional(
            @AuthenticationPrincipal Usuario usuario, @PathVariable Boolean isAtivar) {
        profissionalService.disponibilizaProfissionalServico(usuario, isAtivar);
        return ResponseEntity.noContent().build();
    }
}
