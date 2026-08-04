package br.com.elo.eloapi.exception;

public class FalhaTemporariaEnvioException extends RuntimeException {

    public FalhaTemporariaEnvioException(String message) {
        super(message);
    }

    public FalhaTemporariaEnvioException(String message, Throwable cause) {
        super(message, cause);
    }
}
