package br.com.elo.eloapi.service;


import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.integration.cep.AwesomeApiCepClient;
import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.endereco.dto.Coordenadas;
import br.com.elo.eloapi.model.endereco.dto.EnderecoCreateRQ;
import br.com.elo.eloapi.model.endereco.dto.EnderecoRS;
import br.com.elo.eloapi.model.endereco.mapper.EnderecoMapper;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.model.usuario.dto.UsuarioEditRQ;
import br.com.elo.eloapi.model.usuario.dto.UsuarioRS;
import br.com.elo.eloapi.model.usuario.mapper.UsuarioMapper;
import br.com.elo.eloapi.repository.EnderecoRepository;
import br.com.elo.eloapi.repository.RedisStore;
import br.com.elo.eloapi.repository.UsuarioRepository;
import br.com.elo.eloapi.service.search.SearchOutboxService;
import br.com.elo.eloapi.model.storage.EscopoImagem;
import br.com.elo.eloapi.service.storage.ImagemService;
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
    private final RedisStore redisStore;
    private final ImagemService imagemService;

    @Transactional
    public UsuarioRS editarPerfil(Usuario usuarioAutenticado, UsuarioEditRQ usuarioEditRQ) {
        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.getId()).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado"));

        usuario.setNome(usuarioEditRQ.nome());
        usuario.setSobrenome(usuarioEditRQ.sobrenome());
        usuario.setEmail(usuarioEditRQ.email());
        usuario.setTelCelular(usuarioEditRQ.telContato());
        usuario.setTelWhats(usuarioEditRQ.telContatoZap());
        if (usuarioEditRQ.chaveImagem() != null) {
            imagemService.validarChave(EscopoImagem.PERFIL, usuarioEditRQ.chaveImagem());
            usuario.setUriPerfil(usuarioEditRQ.chaveImagem());
        }

        usuario = usuarioRepository.save(usuario);
        redisStore.deletarNoCache(String.format(RedisStore.KEY_PROFESSIONAL_DETAILS_PATTERN, usuario.getId()));
        searchOutboxService.solicitarReindexacao(usuario.getId());
        return toResponse(usuario);
    }

    public Optional<UsuarioRS> pegarPerfil(Usuario usuario) {
        return usuarioRepository.findById(usuario.getId()).map(this::toResponse);
    }

    private UsuarioRS toResponse(Usuario usuario) {
        return UsuarioMapper.toResponse(usuario, imagemService.resolvedorDeUrl(EscopoImagem.PERFIL));
    }

    @Transactional
    public EnderecoRS salvarEndereco(Usuario usuario, EnderecoCreateRQ enderecoCreateRQ) {
        Endereco endereco = EnderecoMapper.toEntity(enderecoCreateRQ);

        Coordenadas coordenadas = awesomeApiCepClient.buscarCoordenadas(enderecoCreateRQ.cep());

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
        return enderecoRepository.findByUsuarioIdAndStPrincipalTrue(usuario.getId());
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
