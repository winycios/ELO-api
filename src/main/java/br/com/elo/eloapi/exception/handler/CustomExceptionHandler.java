package br.com.elo.eloapi.exception.handler;

import br.com.elo.eloapi.exception.UnauthorizedException;
import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.erro.ModelError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ModelError> exceptionPersonalized(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);

        });
        ModelError err = new ModelError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Erro de validação", errors.toString(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ModelError> exceptionPersonalized(ResourceNotFound e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ModelError err = new ModelError(Instant.now(), status.value(), status.toString(), e.getMessage(),
                request.getRequestURI());

        logger.info(err.log());

        return ResponseEntity.status(status).body(err);
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ModelError> exceptionPersonalized(UnauthorizedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ModelError err = new ModelError(Instant.now(), status.value(), status.toString(), e.getMessage(),
                request.getRequestURI());

        logger.info(err.log());
        return ResponseEntity.status(status).body(err);
    }
}