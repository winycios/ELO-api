package br.com.elo.eloapi.exception.handler;

import br.com.elo.eloapi.exception.BadRequestException;
import br.com.elo.eloapi.exception.ConflictException;
import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.exception.UnauthorizedException;
import br.com.elo.eloapi.model.erro.ModelError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ModelError> exceptionPersonalized(MethodArgumentNotValidException ex, HttpServletRequest request
    ) {
        String errorResponse = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s - %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("\n"));

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ModelError err = new ModelError(Instant.now(), status.value(), "Erro de validação", errorResponse, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ModelError> exceptionPersonalized(ResourceNotFound e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ModelError err = new ModelError(Instant.now(), status.value(), status.toString(), e.getMessage(),
                request.getRequestURI());

        logger.info(err.log());

        return ResponseEntity.status(status).body(err);
    }


    @ExceptionHandler({UnauthorizedException.class, BadCredentialsException.class})
    public ResponseEntity<ModelError> exceptionPersonalized(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ModelError err = new ModelError(Instant.now(), status.value(), status.toString(), Objects.equals(e.getMessage(), "Bad credentials") ? "Email ou senha inválido" : e.getMessage(),
                request.getRequestURI());

        logger.info(err.log());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ModelError> exceptionPersonalized(ConflictException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ModelError err = new ModelError(Instant.now(), status.value(), status.toString(), e.getMessage(),
                request.getRequestURI());

        logger.info(err.log());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ModelError> exceptionPersonalized(BadRequestException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ModelError err = new ModelError(Instant.now(), status.value(), status.toString(), e.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ModelError> handleUnexpectedException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ModelError err = new ModelError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Ocorreu um erro interno.",
                request.getRequestURI()
        );

        logger.error("Erro não tratado em {}", request.getRequestURI(), e);
        return ResponseEntity.status(status).body(err);
    }
}
