package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.search.dto.BuscaProfissionalFiltro;
import br.com.elo.eloapi.model.search.dto.BuscaProfissionalRS;
import br.com.elo.eloapi.model.search.dto.OrdenacaoBuscaProfissional;
import br.com.elo.eloapi.service.search.BuscaProfissionalService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/busca")
@RequiredArgsConstructor
public class BuscaController {

    private final BuscaProfissionalService buscaProfissionalService;

    @GetMapping("/profissionais")
    public ResponseEntity<BuscaProfissionalRS> buscarProfissionais(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) @DecimalMin("0.0") @DecimalMax("5.0") Double avaliacaoMinima,
            @RequestParam(required = false) @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam(required = false) @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam(defaultValue = "50") @DecimalMin("0.1") @DecimalMax("500.0") Double distanciaKm,
            @RequestParam(defaultValue = "RECOMENDADOS") OrdenacaoBuscaProfissional ordenacao,
            @RequestParam(defaultValue = "0") @Min(0) Integer pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer tamanho
    ) {
        return ResponseEntity.ok(buscaProfissionalService.buscar(new BuscaProfissionalFiltro(texto, categoriaId, avaliacaoMinima, latitude, longitude, distanciaKm, ordenacao, pagina, tamanho)));
    }
}
