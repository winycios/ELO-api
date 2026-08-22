package br.com.elo.eloapi.exception.handler;

import br.com.elo.eloapi.exception.*;
import br.com.elo.eloapi.model.erro.ModelError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ModelError err = new ModelError(Instant.now(), status.value(), "Erro de validação", errorResponse, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ModelError> exceptionPersonalized(ConstraintViolationException ex, HttpServletRequest request) {
        String errorResponse = ex.getConstraintViolations()
                .stream()
                .map(violation -> String.format("%s - %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining("\n"));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ModelError err = new ModelError(Instant.now(), status.value(), "Erro de validação", errorResponse, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ModelError> exceptionPersonalized(HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ModelError err = new ModelError(
                Instant.now(),
                status.value(),
                "Erro de validação",
                "O corpo da requisição está inválido.",
                request.getRequestURI()
        );
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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ModelError> exceptionPersonalized(MaxUploadSizeExceededException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        ModelError err = new ModelError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "A imagem enviada excede o tamanho máximo permitido.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(SearchUnavailableException.class)
    public ResponseEntity<ModelError> exceptionPersonalized(SearchUnavailableException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        ModelError err = new ModelError(Instant.now(), status.value(), status.toString(), e.getMessage(),
                request.getRequestURI());
        logger.warn("Servico de busca indisponivel em {}", request.getRequestURI(), e);
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            UnsatisfiedServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ModelError> invalidRequestParameters(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ModelError err = new ModelError(
                Instant.now(),
                status.value(),
                "Erro de validação",
                "Os parâmetros enviados são inválidos ou não atendem aos requisitos da requisição.",
                request.getRequestURI()
        );

        logger.warn("Parâmetros inválidos em {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ModelError> requestException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ModelError err = new ModelError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Endpoint não encontrado",
                request.getRequestURI()
        );

        logger.error("Endpoint não encontrado {}", request.getRequestURI(), e);
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
