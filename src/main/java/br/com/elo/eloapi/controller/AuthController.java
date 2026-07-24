package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.usuario.dto.LoginRQ;
import br.com.elo.eloapi.model.usuario.dto.LoginResponseRS;
import br.com.elo.eloapi.model.usuario.dto.RefreshTokenRQ;
import br.com.elo.eloapi.model.usuario.dto.UsuarioCreateRQ;
import br.com.elo.eloapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authenticationService;

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseRS> refreshToken(@RequestBody RefreshTokenRQ refreshToken) {
        return ResponseEntity.ok(authenticationService.refresh(refreshToken.refreshToken()));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseRS> authenticate(@RequestBody @Valid LoginRQ loginUserDto) {
        return ResponseEntity.ok(authenticationService.authenticate(loginUserDto));
    }

    @PostMapping("/create")
    public ResponseEntity<Void> register(@RequestBody @Valid UsuarioCreateRQ usuarioCreateRQ) {
        authenticationService.createUser(usuarioCreateRQ);
        return ResponseEntity.noContent().build();
    }


}