package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.avaliacao.dto.AvaliacaoOrcamentoRQ;
import br.com.elo.eloapi.model.avaliacao.dto.AvaliacaoOrcamentoRS;
import br.com.elo.eloapi.model.orcamento.dto.*;
import br.com.elo.eloapi.model.publicacao.dto.CursorPageRS;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.service.OrcamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @GetMapping("agenda/listar")
    public ResponseEntity<Map<String, List<OrcamentoListagemProfissionalRS>>> listarAgendaProfissional(@AuthenticationPrincipal Usuario usuario, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate dataInicio) {
        return orcamentoService.listarAgenda(usuario, dataInicio).map(listagem -> ResponseEntity.ok().body(listagem)).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/profissional/listar")
    public ResponseEntity<CursorPageRS<OrcamentoListagemProfissionalRS>> listarOrcamentosProfissional(@AuthenticationPrincipal Usuario usuario, @RequestParam(required = true) String status, @RequestParam(required = false) String cursor, @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer tamanho) {
        return ResponseEntity.ok(orcamentoService.listarOrcamentosProfissional(usuario, status, cursor, tamanho));
    }

    @GetMapping("/profissional/{orcamentoId}")
    public ResponseEntity<OrcamentoDetalheProfissionalRS> buscarOrcamentoPorIdProfissional(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long orcamentoId) {
        return ResponseEntity.ok(orcamentoService.buscarOrcamentoPorIdProfissional(usuario, orcamentoId));
    }

    @PostMapping("/profissional/{orcamentoId}/final")
    public ResponseEntity<OrcamentoDetalheProfissionalRS> enviarOrcamentoFinal(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long orcamentoId, @RequestBody @Valid OrcamentoFinalCreateRQ request) {
        return ResponseEntity.ok(orcamentoService.enviarOrcamentoFinal(usuario, orcamentoId, request));
    }

    @PatchMapping("/profissional/{orcamentoId}/recusar")
    public ResponseEntity<OrcamentoDetalheProfissionalRS> recusarOrcamento(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long orcamentoId, @RequestBody @Valid OrcamentoCancelamentoRQ request) {
        return ResponseEntity.ok(orcamentoService.recusarOrcamento(usuario, orcamentoId, request));
    }

    @PatchMapping("/profissional/{orcamentoId}/concluir")
    public ResponseEntity<OrcamentoDetalheProfissionalRS> concluirOrcamento(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long orcamentoId, @RequestBody @Valid OrcamentoConclusaoRQ request) {
        return ResponseEntity.ok(orcamentoService.concluirOrcamento(usuario, orcamentoId, request));
    }

    @PostMapping("/profissional/{orcamentoId}/avaliar")
    public ResponseEntity<AvaliacaoOrcamentoRS> avaliarCliente(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long orcamentoId, @RequestBody @Valid AvaliacaoOrcamentoRQ request) {
        AvaliacaoOrcamentoRS response = orcamentoService.avaliarCliente(usuario, orcamentoId, request);
        return ResponseEntity.created(URI.create("/api/orcamento/" + orcamentoId + "/avaliacoes/" + response.id()))
                .body(response);
    }

    @GetMapping("/{orcamentoId}")
    public ResponseEntity<OrcamentoDetalheRS> buscarOrcamentoPorId(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long orcamentoId) {
        return ResponseEntity.ok(orcamentoService.buscarOrcamentoPorId(usuario, orcamentoId));
    }

    @PatchMapping("usuario/{orcamentoId}/aprovar")
    public ResponseEntity<OrcamentoDetalheRS> aprovarOrcamentoFinal(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long orcamentoId) {
        return ResponseEntity.ok(orcamentoService.aprovarOrcamentoFinal(usuario, orcamentoId));
    }

    @PatchMapping("usuario/{orcamentoId}/cancelar")
    public ResponseEntity<OrcamentoDetalheRS> cancelarOrcamentoCliente(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long orcamentoId, @RequestBody @Valid OrcamentoCancelamentoRQ request) {
        return ResponseEntity.ok(orcamentoService.cancelarOrcamentoCliente(usuario, orcamentoId, request));
    }

    @PostMapping("usuario/{orcamentoId}/avaliar")
    public ResponseEntity<AvaliacaoOrcamentoRS> avaliarProfissional(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long orcamentoId, @RequestBody @Valid AvaliacaoOrcamentoRQ request) {
        AvaliacaoOrcamentoRS response = orcamentoService.avaliarProfissional(usuario, orcamentoId, request);
        return ResponseEntity.created(URI.create("/api/orcamento/" + orcamentoId + "/avaliacoes/" + response.id())).body(response);
    }

    @PostMapping
    public ResponseEntity<OrcamentoRS> solicitarOrcamento(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid OrcamentoCreateRQ request) {
        OrcamentoRS response = orcamentoService.solicitarOrcamento(usuario, request);
        return ResponseEntity.created(URI.create("/api/orcamento/" + response.id())).body(response);
    }
}
