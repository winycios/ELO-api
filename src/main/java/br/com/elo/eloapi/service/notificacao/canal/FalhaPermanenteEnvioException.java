package br.com.elo.eloapi.service.notificacao.canal;

public class FalhaPermanenteEnvioException extends RuntimeException {

    public FalhaPermanenteEnvioException(String message) {
        super(message);
    }

    public FalhaPermanenteEnvioException(String message, Throwable cause) {
        super(message, cause);
    }
}
