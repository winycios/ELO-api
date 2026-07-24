package br.com.elo.eloapi.service;


import br.com.elo.eloapi.exception.ConflictException;
import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.exception.UnauthorizedException;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.redis.RefreshTokenData;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.model.usuario.dto.LoginDTO;
import br.com.elo.eloapi.model.usuario.dto.LoginResponseDTO;
import br.com.elo.eloapi.model.usuario.dto.UsuarioCreateDTO;
import br.com.elo.eloapi.model.usuario.mapper.UsuarioMapper;
import br.com.elo.eloapi.repository.ProfissionalRepository;
import br.com.elo.eloapi.repository.RedisStore;
import br.com.elo.eloapi.repository.UsuarioRepository;
import br.com.elo.eloapi.service.search.SearchOutboxService;
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

    @Value("${security.jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;


    public AuthService(UsuarioRepository usuarioRepository, ProfissionalRepository profissionalRepository,
                       AuthenticationManager authenticationManager, UsuarioMapper usuarioMapper,
                       JwtService jwtService, RedisStore redisStore, SearchOutboxService searchOutboxService) {
        this.usuarioRepository = usuarioRepository;
        this.profissionalRepository = profissionalRepository;
        this.authenticationManager = authenticationManager;
        this.usuarioMapper = usuarioMapper;
        this.jwtService = jwtService;
        this.redisStore = redisStore;
        this.searchOutboxService = searchOutboxService;
    }

    @Transactional
    public void createUser(UsuarioCreateDTO usuarioCreateDto) {
        if (usuarioRepository.findByEmail(usuarioCreateDto.email()).isPresent()) {
            throw new ConflictException("Email já cadastrado!");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioCreateDto);
        usuario = usuarioRepository.save(usuario);
        Profissional profissional = profissionalRepository.save(new Profissional(usuario, !usuarioCreateDto.cadastroAcao().isCadastrarUsuario()));
        searchOutboxService.solicitarReindexacao(profissional.getId());
    }

    public LoginResponseDTO authenticate(LoginDTO loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.senha()));
        return buildLoginResponse(profissionalRepository.findByUsuarioEmail(loginDto.email()).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado!")), false, loginDto.deviceCode());
    }

    public LoginResponseDTO refresh(String token) {
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
        RefreshTokenData tokenData = redisStore.findHset(String.format(RedisStore.KEY_TEMPLATE_AUTH, profissional.getUsuario().getId()), deviceCode, RefreshTokenData.class).orElseThrow(() -> new UnauthorizedException("Sessão do dispositivo não encontrada ou expirada."));

        if (!MessageDigest.isEqual(tokenData.tokenHash().getBytes(StandardCharsets.UTF_8), sha256(token).getBytes(StandardCharsets.UTF_8))) {
            throw new UnauthorizedException("Refresh token não corresponde ao dispositivo.");
        }

        return buildLoginResponse(profissional, true, deviceCode);
    }

    private LoginResponseDTO buildLoginResponse(Profissional profissional, Boolean isRefresh, String deviceCode) {
        String accessToken = jwtService.generateToken(profissional.getUsuario(), deviceCode, accessTokenExpiration);
        String refreshToken = isRefresh ? null : jwtService.generateToken(profissional.getUsuario(), deviceCode, refreshTokenExpiration);

        // No login (isRefresh == false) gravamos/renovamos a sessão do dispositivo no Redis.
        if (!isRefresh) {
            RefreshTokenData data = new RefreshTokenData(
                    jwtService.extractTokenId(refreshToken),
                    sha256(refreshToken),
                    profissional.getUsuario().getId());
            redisStore.saveHSet(String.format(RedisStore.KEY_TEMPLATE_AUTH, profissional.getUsuario().getId()), deviceCode, data, Duration.ofMillis(refreshTokenExpiration));
        }

        return new LoginResponseDTO(profissional.getId(), accessToken, refreshToken, profissional.getUsuario().nomeCompleto(), profissional.getUsuario().getUriPerfil(), profissional.getUriPerfil(), profissional.getStHabilitado(), profissional.getUsuario().getStHabilitado());
    }
}