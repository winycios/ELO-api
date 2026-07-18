package br.com.elo.eloapi.model.usuario.mapper;


import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.model.usuario.dto.UsuarioCreateDTO;
import br.com.elo.eloapi.model.usuario.dto.UsuarioRS;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class UsuarioMapper {

    private final PasswordEncoder passwordEncoder;

    public static UsuarioRS toResponse(Usuario usuario) {
        return new UsuarioRS(usuario.getId(), usuario.nomeCompleto(), usuario.getEmail(), usuario.getTelCelular(), usuario.getTelWhats(), 1L, 2L, 4.9);
    }

    public Usuario toEntity(UsuarioCreateDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setSobrenome(dto.getSobrenome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelCelular(dto.getTelContato());
        if (dto.getIsDuplicarTel()) {
            usuario.setTelWhats(dto.getTelContato());
        }

        usuario.setStHabilitado(!dto.getCadastroAcao().isCadastrarProfissional());

        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        return usuario;
    }
}