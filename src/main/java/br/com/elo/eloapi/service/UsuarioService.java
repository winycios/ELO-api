package br.com.elo.eloapi.service;


import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.integration.cep.AwesomeApiCepClient;
import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.endereco.dto.Coordenadas;
import br.com.elo.eloapi.model.endereco.dto.EnderecoCreateDTO;
import br.com.elo.eloapi.model.endereco.dto.EnderecoRS;
import br.com.elo.eloapi.model.endereco.mapper.EnderecoMapper;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.model.usuario.dto.UsuarioEditDTO;
import br.com.elo.eloapi.model.usuario.dto.UsuarioRS;
import br.com.elo.eloapi.model.usuario.mapper.UsuarioMapper;
import br.com.elo.eloapi.repository.EnderecoRepository;
import br.com.elo.eloapi.repository.UsuarioRepository;
import br.com.elo.eloapi.service.search.SearchOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final AwesomeApiCepClient awesomeApiCepClient;
    private final SearchOutboxService searchOutboxService;

    @Transactional
    public UsuarioRS editarPerfil(Usuario usuarioAutenticado, UsuarioEditDTO usuarioEditDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.getId()).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado"));

        usuario.setNome(usuarioEditDTO.getNome());
        usuario.setSobrenome(usuarioEditDTO.getSobrenome());
        usuario.setEmail(usuarioEditDTO.getEmail());
        usuario.setTelCelular(usuarioEditDTO.getTelContato());
        usuario.setTelWhats(usuarioEditDTO.getTelContatoZap());

        usuario = usuarioRepository.save(usuario);
        searchOutboxService.solicitarReindexacao(usuario.getId());
        return UsuarioMapper.toResponse(usuario);
    }

    public Optional<Usuario> pegarPerfil(Usuario usuario) {
        return usuarioRepository.findById(usuario.getId());
    }

    @Transactional
    public EnderecoRS salvarEndereco(Usuario usuario, EnderecoCreateDTO enderecoCreateDTO) {
        Endereco endereco = EnderecoMapper.toEntity(enderecoCreateDTO);

        Coordenadas coordenadas = awesomeApiCepClient.buscarCoordenadas(enderecoCreateDTO.getCep());

        if (!enderecoRepository.existsEnderecoByUsuarioId(usuario.getId())) {
            endereco.setStPrincipal(true);
        }

        endereco.setUsuario(usuario);
        endereco.setStAtivo(true);
        endereco.setNrLatitude(coordenadas.latitude());
        endereco.setNrLongitude(coordenadas.longitude());

        return EnderecoMapper.toResponse(enderecoRepository.save(endereco));
    }

    public Optional<Endereco> buscarPrincipal(Usuario usuario) {
        return enderecoRepository.findByUsuarioIdAndStPrincipal(usuario.getId(), true);
    }

    public Optional<List<Endereco>> buscarEnderecoAtivos(Usuario usuario) {
        return enderecoRepository.findByUsuarioIdAndStAtivoTrue(usuario.getId());
    }

    @Transactional
    public void mudarStPrincipalEndereco(Usuario usuario, Long enderecoId) {
        Endereco novoPrincipal = enderecoRepository.findByIdAndUsuarioIdAndStAtivoTrue(enderecoId, usuario.getId()).orElseThrow(() -> new ResourceNotFound("Endereço ativo não encontrado para este usuário"));

        if (Boolean.TRUE.equals(novoPrincipal.getStPrincipal())) {
            return;
        }

        enderecoRepository.desmarcarEnderecoPrincipal(usuario.getId());
        novoPrincipal.setStPrincipal(true);
        enderecoRepository.save(novoPrincipal);
    }

    public void desativarEndereco(Usuario usuario, Long enderecoId) {
        Endereco endereco = enderecoRepository.findByIdAndUsuarioIdAndStAtivoTrue(enderecoId, usuario.getId()).orElseThrow(() -> new ResourceNotFound("Endereço ativo não encontrado para este usuário"));
        endereco.setStAtivo(false);
        enderecoRepository.save(endereco);
    }
}
