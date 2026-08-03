package br.com.elo.eloapi.controller;

import br.com.elo.eloapi.model.notificacao.dto.ContagemNotificacaoRS;
import br.com.elo.eloapi.model.notificacao.dto.DispositivoRegistroRQ;
import br.com.elo.eloapi.model.notificacao.dto.NotificacaoRS;
import br.com.elo.eloapi.model.publicacao.dto.CursorPageRS;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.service.notificacao.DispositivoUsuarioService;
import br.com.elo.eloapi.service.notificacao.NotificacaoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final DispositivoUsuarioService dispositivoService;

    @PutMapping("/dispositivos")
    public ResponseEntity<Void> registrarDispositivo(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid DispositivoRegistroRQ request) {
        dispositivoService.registrar(usuario, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/dispositivos/{codigoDispositivo}")
    public ResponseEntity<Void> desativarDispositivo(@AuthenticationPrincipal Usuario usuario, @PathVariable @NotBlank String codigoDispositivo) {
        dispositivoService.desativar(usuario, codigoDispositivo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<CursorPageRS<NotificacaoRS>> listar(@AuthenticationPrincipal Usuario usuario, @RequestParam(required = false) String cursor, @RequestParam(defaultValue = "20") @Min(1) @Max(50) Integer tamanho) {
        return ResponseEntity.ok(notificacaoService.listar(usuario, cursor, tamanho));
    }

    @GetMapping("/nao-lidas")
    public ResponseEntity<ContagemNotificacaoRS> contarNaoLidas(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(new ContagemNotificacaoRS(notificacaoService.contarNaoLidas(usuario)));
    }

    @PatchMapping("/{notificacaoId}/lida")
    public ResponseEntity<NotificacaoRS> marcarComoLida(@AuthenticationPrincipal Usuario usuario, @PathVariable @Positive Long notificacaoId) {
        return ResponseEntity.ok(notificacaoService.marcarComoLida(usuario, notificacaoId));
    }

    @PatchMapping("/lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(@AuthenticationPrincipal Usuario usuario) {
        notificacaoService.marcarTodasComoLidas(usuario);
        return ResponseEntity.noContent().build();
    }
}
