package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.categoria.dto.CategoriaRS;
import br.com.elo.eloapi.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping("/listar")
    public ResponseEntity<List<CategoriaRS>> listar() {
        return  ResponseEntity.ok().body(categoriaService.listar());
    }
}
