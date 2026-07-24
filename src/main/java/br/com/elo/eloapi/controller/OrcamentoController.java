package br.com.elo.eloapi.controller;


import br.com.elo.eloapi.model.servico.dto.ServicoCreateRQ;
import br.com.elo.eloapi.model.servico.dto.ServicoRS;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.service.OrcamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/orcamento")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @PostMapping("/servico")
    public ResponseEntity<ServicoRS> buscarHorario(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid ServicoCreateRQ servicoCreateRQ) {
        return null;
    }
}
