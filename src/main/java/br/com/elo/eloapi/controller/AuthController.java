package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.usuario.dto.LoginDTO;
import br.com.elo.eloapi.model.usuario.dto.LoginResponseDTO;
import br.com.elo.eloapi.model.usuario.dto.RefreshTokenDTO;
import br.com.elo.eloapi.model.usuario.dto.UsuarioCreateDTO;
import br.com.elo.eloapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authenticationService;

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody RefreshTokenDTO refreshToken) {
        return ResponseEntity.ok(authenticationService.refresh(refreshToken.refreshToken()));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody @Valid LoginDTO loginUserDto) {
        return ResponseEntity.ok(authenticationService.authenticate(loginUserDto));
    }

    @PostMapping("/create")
    public ResponseEntity<Void> register(@RequestBody @Valid UsuarioCreateDTO usuarioCreateDto) {
        authenticationService.createUser(usuarioCreateDto);
        return ResponseEntity.noContent().build();
    }


}