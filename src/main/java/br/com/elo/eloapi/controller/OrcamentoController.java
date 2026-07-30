package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.orcamento.dto.*;
import br.com.elo.eloapi.model.publicacao.dto.CursorPageRS;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.service.OrcamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/orcamento")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @GetMapping("/servico/{servicoId}/horarios-disponiveis")
    public ResponseEntity<HorariosDisponiveisRS> buscarHorariosDisponiveis(@PathVariable @Positive Long servicoId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataReferencia) {
        return ResponseEntity.ok(orcamentoService.buscarHorariosDisponiveis(servicoId, dataReferencia));
    }

    @GetMapping("/listar")
    public ResponseEntity<CursorPageRS<OrcamentoListagemRS>> listarOrcamentos(@AuthenticationPrincipal Usuario usuario, @RequestParam(required = false) String status, @RequestParam(required = false) String cursor, @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer tamanho) {
        return ResponseEntity.ok(orcamentoService.listarOrcamentos(usuario, status, cursor, tamanho));
    }

    @GetMapping("/listarProfisisonal")
    public ResponseEntity<CursorPageRS<OrcamentoListagemProfissionalRS>> listarOrcamentosProfissional(@AuthenticationPrincipal Usuario usuario, @RequestParam(required = false) String status, @RequestParam(required = false) String cursor, @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer tamanho) {
        return ResponseEntity.ok(orcamentoService.listarOrcamentosProfissional(usuario, status, cursor, tamanho));
    }

    @GetMapping("/{orcamentoId}")
    public ResponseEntity<OrcamentoDetalheRS> buscarOrcamentoPorId(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long orcamentoId) {
        return ResponseEntity.ok(orcamentoService.buscarOrcamentoPorId(usuario, orcamentoId));
    }

    @PostMapping
    public ResponseEntity<OrcamentoRS> solicitarOrcamento(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid OrcamentoCreateRQ request) {
        OrcamentoRS response = orcamentoService.solicitarOrcamento(usuario, request);
        return ResponseEntity.created(URI.create("/api/orcamento/" + response.id())).body(response);
    }
}
