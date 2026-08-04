package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.imagem.dto.ImagemUploadRS;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.service.storage.ImagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/imagem")
@RequiredArgsConstructor
public class ImagemController {

    private final ImagemService imagemService;

    @PostMapping(path = "/{escopo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagemUploadRS> enviarImagem(@AuthenticationPrincipal Usuario usuario, @PathVariable String escopo, @RequestPart("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(imagemService.enviar(escopo, arquivo));
    }
}
