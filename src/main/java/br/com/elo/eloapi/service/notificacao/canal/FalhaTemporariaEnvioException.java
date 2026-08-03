package br.com.elo.eloapi.service.notificacao.canal;

public class FalhaTemporariaEnvioException extends RuntimeException {

    public FalhaTemporariaEnvioException(String message) {
        super(message);
    }

    public FalhaTemporariaEnvioException(String message, Throwable cause) {
        super(message, cause);
    }
}
