package br.com.elo.eloapi.service.notificacao.canal;

import br.com.elo.eloapi.model.notificacao.CanalNotificacao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "elo.notification.email.enabled", havingValue = "true")
public class EmailCanalNotificacao implements CanalEnvioNotificacao {

    private final JavaMailSender mailSender;
    private final String remetente;

    public EmailCanalNotificacao(JavaMailSender mailSender, @Value("${elo.notification.email.from}") String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @Override
    public CanalNotificacao getCanal() {
        return CanalNotificacao.EMAIL;
    }

    @Override
    public ResultadoEnvioNotificacao enviar(ComandoEnvioNotificacao comando) {
        if (comando.email() == null || comando.email().isBlank()) {
            throw new FalhaPermanenteEnvioException("Destinatário não possui e-mail válido.");
        }

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(comando.email());
        mensagem.setSubject(comando.titulo());
        mensagem.setText(comando.mensagem() + System.lineSeparator() + System.lineSeparator() + "Acesse o aplicativo Elo para consultar o orçamento.");

        try {
            mailSender.send(mensagem);
            return new ResultadoEnvioNotificacao(null);
        } catch (MailAuthenticationException exception) {
            throw new FalhaPermanenteEnvioException("Falha de autenticação no provedor de e-mail.", exception);
        } catch (MailException exception) {
            throw new FalhaTemporariaEnvioException("Falha temporária ao enviar e-mail.", exception);
        }
    }
}
