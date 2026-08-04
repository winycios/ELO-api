package br.com.elo.eloapi.service.notificacao.canal;

import br.com.elo.eloapi.exception.FalhaPermanenteEnvioException;
import br.com.elo.eloapi.exception.FalhaTemporariaEnvioException;
import br.com.elo.eloapi.implementation.CanalEnvioNotificacao;
import br.com.elo.eloapi.model.notificacao.CanalNotificacao;
import br.com.elo.eloapi.model.notificacao.dto.ComandoEnvioNotificacao;
import br.com.elo.eloapi.model.notificacao.dto.ResultadoEnvioNotificacao;
import br.com.elo.eloapi.repository.DispositivoUsuarioRepository;
import com.google.firebase.messaging.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "elo.notification.push.enabled", havingValue = "true")
public class FirebaseCanalNotificacao implements CanalEnvioNotificacao {

    private static final Set<MessagingErrorCode> ERROS_TEMPORARIOS = EnumSet.of(MessagingErrorCode.INTERNAL, MessagingErrorCode.UNAVAILABLE, MessagingErrorCode.QUOTA_EXCEEDED);

    private final FirebaseMessaging firebaseMessaging;
    private final DispositivoUsuarioRepository dispositivoRepository;

    public FirebaseCanalNotificacao(FirebaseMessaging firebaseMessaging, DispositivoUsuarioRepository dispositivoRepository) {
        this.firebaseMessaging = firebaseMessaging;
        this.dispositivoRepository = dispositivoRepository;
    }

    @Override
    public CanalNotificacao getCanal() {
        return CanalNotificacao.PUSH;
    }

    @Override
    public ResultadoEnvioNotificacao enviar(ComandoEnvioNotificacao comando) {
        if (comando.identificadorFcm() == null || comando.identificadorFcm().isBlank()) {
            throw new FalhaPermanenteEnvioException("Dispositivo não possui identificador FCM válido.");
        }

        Message.Builder mensagem = Message.builder().setNotification(Notification.builder().setTitle(comando.titulo()).setBody(comando.mensagem()).build()).putAllData(comando.dados());
        mensagem.setFid(comando.identificadorFcm());

        try {
            String resposta = firebaseMessaging.send(mensagem.build());
            return new ResultadoEnvioNotificacao(resposta);
        } catch (FirebaseMessagingException exception) {
            MessagingErrorCode codigo = exception.getMessagingErrorCode();
            if (codigo == MessagingErrorCode.UNREGISTERED) {
                desativarDispositivo(comando.dispositivoId());
                throw new FalhaPermanenteEnvioException("Instalação não registrada no FCM.", exception);
            }
            if (codigo != null && ERROS_TEMPORARIOS.contains(codigo)) {
                throw new FalhaTemporariaEnvioException("FCM temporariamente indisponível: " + codigo, exception);
            }
            throw new FalhaPermanenteEnvioException("FCM rejeitou a mensagem: " + codigo, exception);
        }
    }

    private void desativarDispositivo(Long dispositivoId) {
        if (dispositivoId == null) {
            return;
        }
        dispositivoRepository.desativarPorId(dispositivoId);
    }
}
