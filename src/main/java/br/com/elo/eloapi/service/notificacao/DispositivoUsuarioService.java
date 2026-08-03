package br.com.elo.eloapi.service.notificacao;

import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.notificacao.DispositivoUsuario;
import br.com.elo.eloapi.model.notificacao.dto.DispositivoRegistroRQ;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.repository.DispositivoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DispositivoUsuarioService {

    private final DispositivoUsuarioRepository dispositivoRepository;

    @Transactional
    public void registrar(Usuario usuario, DispositivoRegistroRQ request) {
        String codigo = request.codigoDispositivo().trim();
        String identificador = request.identificadorFcm().trim();

        Optional<DispositivoUsuario> porIdentificador = dispositivoRepository.findByIdentificadorFcm(identificador);
        Optional<DispositivoUsuario> porCodigo = dispositivoRepository.findByUsuarioIdAndCodigoDispositivo(usuario.getId(), codigo);

        if (porIdentificador.isPresent() && porCodigo.isPresent() && !porIdentificador.get().getId().equals(porCodigo.get().getId())) {
            DispositivoUsuario registroAnterior = porIdentificador.get();
            registroAnterior.setAtivo(false);
            registroAnterior.setIdentificadorFcm("invalidado:" + registroAnterior.getId() + ":" + UUID.randomUUID());
            dispositivoRepository.saveAndFlush(registroAnterior);
            porIdentificador = Optional.empty();
        }

        DispositivoUsuario dispositivo = porCodigo.isPresent() ? porCodigo.get() : porIdentificador.orElseGet(DispositivoUsuario::new);
        dispositivo.setUsuario(usuario);
        dispositivo.setCodigoDispositivo(codigo);
        dispositivo.setIdentificadorFcm(identificador);
        dispositivo.setTipoIdentificador(request.tipoIdentificador());
        dispositivo.setPlataforma(request.plataforma());
        dispositivo.setAtivo(true);
        dispositivoRepository.save(dispositivo);
    }

    @Transactional
    public void desativar(Usuario usuario, String codigoDispositivo) {
        DispositivoUsuario dispositivo = dispositivoRepository
                .findByUsuarioIdAndCodigoDispositivo(usuario.getId(), codigoDispositivo.trim())
                .orElseThrow(() -> new ResourceNotFound("Dispositivo não encontrado."));
        dispositivo.setAtivo(false);
        dispositivoRepository.save(dispositivo);
    }
}
