package br.com.elo.eloapi.model.usuario.mapper;


import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.model.usuario.dto.UsuarioCreateDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UsuarioMapper {

    private final PasswordEncoder passwordEncoder;

    public Usuario toEntity(UsuarioCreateDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setSobrenome(dto.getSobrenome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelCelular(dto.getTelContato());
        if (dto.getIsDuplicarTel()) {
        usuario.setTelWhats(dto.getTelContato());
        }
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        return usuario;
    }
}