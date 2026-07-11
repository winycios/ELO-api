package br.com.elo.eloapi.service;


import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.exception.UnauthorizedException;
import br.com.elo.eloapi.model.RqRsRedis.RefreshTokenData;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.model.usuario.dto.LoginDTO;
import br.com.elo.eloapi.model.usuario.dto.LoginResponseDTO;
import br.com.elo.eloapi.model.usuario.dto.UsuarioCreateDTO;
import br.com.elo.eloapi.model.usuario.mapper.UsuarioMapper;
import br.com.elo.eloapi.repository.RefreshTokenStore;
import br.com.elo.eloapi.repository.UsuarioRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

import static br.com.elo.eloapi.Util.Utils.sha256;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;
    private final JwtService jwtService;
    private final RefreshTokenStore refreshTokenStore;

    @Value("${security.jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;


    public AuthService(UsuarioRepository usuarioRepository, AuthenticationManager authenticationManager, UsuarioMapper usuarioMapper, JwtService jwtService, RefreshTokenStore refreshTokenStore) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.usuarioMapper = usuarioMapper;
        this.jwtService = jwtService;
        this.refreshTokenStore = refreshTokenStore;
    }

    public void createUser(UsuarioCreateDTO usuarioCreateDto) {
        if (usuarioRepository.findByEmail(usuarioCreateDto.getEmail()).isPresent()) {
            throw new UnauthorizedException("Email já cadastrado!");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioCreateDto);
        usuarioRepository.save(usuario);
    }

    public LoginResponseDTO authenticate(LoginDTO loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getSenha()));
        return buildLoginResponse(usuarioRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado!")),false, loginDto.getDeviceCode());
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

        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado!"));
        RefreshTokenData tokenData = refreshTokenStore.findHset(String.format(RefreshTokenStore.KEY_TEMPLATE_AUTH, usuario.getId()), deviceCode, RefreshTokenData.class).orElseThrow(() -> new UnauthorizedException("Sessão do dispositivo não encontrada ou expirada."));

        if (!MessageDigest.isEqual(tokenData.tokenHash().getBytes(StandardCharsets.UTF_8), sha256(token).getBytes(StandardCharsets.UTF_8))) {
            throw new UnauthorizedException("Refresh token não corresponde ao dispositivo.");
        }

        return buildLoginResponse(usuario, true, deviceCode);
    }

    private LoginResponseDTO buildLoginResponse(Usuario usuario, Boolean isRefresh, String deviceCode) {
        String accessToken = jwtService.generateToken(usuario, deviceCode, accessTokenExpiration);
        String refreshToken = isRefresh ? null : jwtService.generateToken(usuario, deviceCode, refreshTokenExpiration);

        // No login (isRefresh == false) gravamos/renovamos a sessão do dispositivo no Redis.
        if (!isRefresh) {
            RefreshTokenData data = new RefreshTokenData(
                    jwtService.extractTokenId(refreshToken),
                    sha256(refreshToken),
                    usuario.getId());
            refreshTokenStore.saveHSet(String.format(RefreshTokenStore.KEY_TEMPLATE_AUTH, usuario.getId()), deviceCode, data, Duration.ofMillis(refreshTokenExpiration));
        }

        return new LoginResponseDTO(usuario.getId(), accessToken, refreshToken);
    }
}