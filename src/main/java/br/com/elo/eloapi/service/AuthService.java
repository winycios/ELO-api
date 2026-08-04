package br.com.elo.eloapi.service;


import br.com.elo.eloapi.exception.ConflictException;
import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.exception.UnauthorizedException;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.redis.RefreshTokenData;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.model.usuario.dto.LoginRQ;
import br.com.elo.eloapi.model.usuario.dto.LoginResponseRS;
import br.com.elo.eloapi.model.usuario.dto.UsuarioCreateRQ;
import br.com.elo.eloapi.model.usuario.mapper.UsuarioMapper;
import br.com.elo.eloapi.repository.ProfissionalRepository;
import br.com.elo.eloapi.repository.RedisStore;
import br.com.elo.eloapi.repository.UsuarioRepository;
import br.com.elo.eloapi.service.search.SearchOutboxService;
import br.com.elo.eloapi.model.storage.EscopoImagem;
import br.com.elo.eloapi.service.storage.ImagemService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

import static br.com.elo.eloapi.Util.Utils.sha256;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ProfissionalRepository profissionalRepository;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;
    private final JwtService jwtService;
    private final RedisStore redisStore;
    private final SearchOutboxService searchOutboxService;
    private final ImagemService imagemService;

    @Value("${security.jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;


    public AuthService(UsuarioRepository usuarioRepository, ProfissionalRepository profissionalRepository,
                       AuthenticationManager authenticationManager, UsuarioMapper usuarioMapper,
                       JwtService jwtService, RedisStore redisStore, SearchOutboxService searchOutboxService,
                       ImagemService imagemService) {
        this.usuarioRepository = usuarioRepository;
        this.profissionalRepository = profissionalRepository;
        this.authenticationManager = authenticationManager;
        this.usuarioMapper = usuarioMapper;
        this.jwtService = jwtService;
        this.redisStore = redisStore;
        this.searchOutboxService = searchOutboxService;
        this.imagemService = imagemService;
    }

    @Transactional
    public void createUser(UsuarioCreateRQ usuarioCreateRQ) {
        if (usuarioRepository.findByEmail(usuarioCreateRQ.email()).isPresent()) {
            throw new ConflictException("Email já cadastrado!");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioCreateRQ);
        usuario = usuarioRepository.save(usuario);
        Profissional profissional = profissionalRepository.save(new Profissional(usuario, !usuarioCreateRQ.cadastroAcao().isCadastrarUsuario()));
        searchOutboxService.solicitarReindexacao(profissional.getId());
    }

    public LoginResponseRS authenticate(LoginRQ loginRQ) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRQ.email(), loginRQ.senha()));
        return buildLoginResponse(profissionalRepository.findByUsuarioEmail(loginRQ.email()).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado!")), false, loginRQ.deviceCode());
    }

    public LoginResponseRS refresh(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Refresh token não informado.");
        }

        final String email;
        final String deviceCode;
        try {
            // parseSignedClaims já valida assinatura e expiração do JWT
            email = jwtService.extractUsername(token);
            deviceCode = jwtService.extractDeviceCode(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Refresh token inválido ou expirado.");
        }

        Profissional profissional = profissionalRepository.findByUsuarioEmail(email).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado!"));
        RefreshTokenData tokenData = redisStore.buscarNoCache(String.format(RedisStore.KEY_TEMPLATE_AUTH, profissional.getUsuario().getId()), deviceCode, RefreshTokenData.class);

        if (tokenData == null) {
            throw new UnauthorizedException("Sessão do dispositivo não encontrada ou expirada.");
        }

        if (!MessageDigest.isEqual(tokenData.tokenHash().getBytes(StandardCharsets.UTF_8), sha256(token).getBytes(StandardCharsets.UTF_8))) {
            throw new UnauthorizedException("Refresh token não corresponde ao dispositivo.");
        }

        return buildLoginResponse(profissional, true, deviceCode);
    }

    private LoginResponseRS buildLoginResponse(Profissional profissional, Boolean isRefresh, String deviceCode) {
        String accessToken = jwtService.generateToken(profissional.getUsuario(), deviceCode, accessTokenExpiration);
        String refreshToken = isRefresh ? null : jwtService.generateToken(profissional.getUsuario(), deviceCode, refreshTokenExpiration);

        // No login (isRefresh == false) gravamos/renovamos a sessão do dispositivo no Redis.
        if (!isRefresh) {
            RefreshTokenData data = new RefreshTokenData(
                    jwtService.extractTokenId(refreshToken),
                    sha256(refreshToken),
                    profissional.getUsuario().getId());
            redisStore.salvarNoCache(String.format(RedisStore.KEY_TEMPLATE_AUTH, profissional.getUsuario().getId()), deviceCode, data, Duration.ofMillis(refreshTokenExpiration));
        }

        return new LoginResponseRS(
                profissional.getId(),
                accessToken,
                refreshToken,
                profissional.getUsuario().nomeCompleto(),
                imagemService.urlLeitura(EscopoImagem.PERFIL, profissional.getUsuario().getUriPerfil()),
                imagemService.urlLeitura(EscopoImagem.PERFIL, profissional.getUriPerfil()),
                profissional.getStHabilitado(),
                profissional.getUsuario().getStHabilitado()
        );
    }
}
