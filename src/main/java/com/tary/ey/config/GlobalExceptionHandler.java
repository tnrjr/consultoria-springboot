package com.tary.ey.config;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // 400: payload inválido (erros de bean validation no body)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                 HttpServletRequest req) {
        List<ApiError.FieldViolation> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .toList();

        return ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                "Existem erros de validação no corpo da requisição",
                req.getRequestURI(),
                fields
        );
    }

    // 400: validação em parâmetros (query/path) com @Validated
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolation(ConstraintViolationException ex,
                                              HttpServletRequest req) {
        var fields = ex.getConstraintViolations().stream()
                .map(v -> new ApiError.FieldViolation(v.getPropertyPath().toString(), v.getMessage()))
                .toList();

        return ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                "Existem erros de validação em parâmetros",
                req.getRequestURI(),
                fields
        );
    }

    // 400: erros de regra simples do serviço
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                ex.getMessage(),
                req.getRequestURI(),
                List.of()
        );
    }

    // 409: violações de chave única/foreign key, etc
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return ApiError.of(
                HttpStatus.CONFLICT.value(),
                "Conflito de dados",
                "Violação de integridade no banco de dados",
                req.getRequestURI(),
                List.of()
        );
    }

    // fallback (500) – deixe por último
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpected(Exception ex, HttpServletRequest req) {
        return ApiError.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                "Ocorreu um erro inesperado",
                req.getRequestURI(),
                List.of()
        );
    }

    private ApiError.FieldViolation mapFieldError(FieldError fe) {
        var field = fe.getField();
        var msg = fe.getDefaultMessage();
        return new ApiError.FieldViolation(field, msg);
    }

    // DTO do erro (simples, legível e estável)
    public record ApiError(
            OffsetDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            List<FieldViolation> fieldErrors
    ) {
        public static ApiError of(int status, String error, String message, String path, List<FieldViolation> fields) {
            return new ApiError(OffsetDateTime.now(), status, error, message, path, fields);
        }
        public record FieldViolation(String field, String message) {}
    }
}

