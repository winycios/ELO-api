package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.endereco.dto.EnderecoCreateDTO;
import br.com.elo.eloapi.model.endereco.dto.EnderecoRS;
import br.com.elo.eloapi.model.endereco.mapper.EnderecoMapper;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.model.usuario.dto.UsuarioEditDTO;
import br.com.elo.eloapi.model.usuario.dto.UsuarioRS;
import br.com.elo.eloapi.model.usuario.mapper.UsuarioMapper;
import br.com.elo.eloapi.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PutMapping(value = "/perfil")
    public ResponseEntity<UsuarioRS> editarPerfil(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid UsuarioEditDTO usuarioEditDTO) {
        return ResponseEntity.ok().body(usuarioService.editarPerfil(usuario, usuarioEditDTO));
    }

    @GetMapping(value = "/perfil")
    public ResponseEntity<UsuarioRS> listarPerfil(@AuthenticationPrincipal Usuario usuario) {
        return usuarioService.pegarPerfil(usuario).map(usuarioDto -> ResponseEntity.ok().body(UsuarioMapper.toResponse(usuarioDto))).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping(value = "/endereco")
    public ResponseEntity<EnderecoRS> salvarEndereco(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid EnderecoCreateDTO enderecoCreateDTO) {
        return ResponseEntity.ok().body(usuarioService.salvarEndereco(usuario, enderecoCreateDTO));
    }

    @GetMapping(value = "/endereco/principal")
    public ResponseEntity<EnderecoRS> buscarEnderecoPrincipal(@AuthenticationPrincipal Usuario usuario) {
        Optional<Endereco> endereco = usuarioService.buscarPrincipal(usuario);
        return endereco.map(enderecos -> ResponseEntity.ok().body(EnderecoMapper.toResponse(enderecos))).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping(value = "/endereco/listar")
    public ResponseEntity<List<EnderecoRS>> buscarEnderecoAtivo(@AuthenticationPrincipal Usuario usuario) {
        Optional<List<Endereco>> enderecoList = usuarioService.buscarEnderecoAtivos(usuario);
        return enderecoList.map(enderecos -> ResponseEntity.ok().body(EnderecoMapper.toResponse(enderecos))).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PatchMapping("/endereco/principal/{id}")
    public ResponseEntity<Void> mudarEnderecoPrincipal(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id) {
        usuarioService.mudarStPrincipalEndereco(usuario, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/endereco/principal/{id}")
    public ResponseEntity<Void> desativarEndereco(
            @AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
        usuarioService.desativarEndereco(usuario, id);
        return ResponseEntity.noContent().build();
    }
}
