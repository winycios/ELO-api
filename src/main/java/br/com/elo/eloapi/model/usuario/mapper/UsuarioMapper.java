package br.com.elo.eloapi.model.usuario.mapper;


import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.model.usuario.dto.UsuarioCreateRQ;
import br.com.elo.eloapi.model.usuario.dto.UsuarioRS;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
@AllArgsConstructor
public final class UsuarioMapper {

    private final PasswordEncoder passwordEncoder;

    public static UsuarioRS toResponse(Usuario usuario, UnaryOperator<String> resolverImagem) {
        return new UsuarioRS(usuario.getId(), usuario.nomeCompleto(), resolverImagem.apply(usuario.getUriPerfil()), usuario.getEmail(), usuario.getTelCelular(), usuario.getTelWhats(), 1L, 2L, 4.9);
    }

    public Usuario toEntity(UsuarioCreateRQ dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setSobrenome(dto.sobrenome());
        usuario.setEmail(dto.email());
        usuario.setTelCelular(dto.telContato());
        if (dto.isDuplicarTel()) {
            usuario.setTelWhats(dto.telContato());
        }

        usuario.setStHabilitado(!dto.cadastroAcao().isCadastrarProfissional());

        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        return usuario;
    }
}
