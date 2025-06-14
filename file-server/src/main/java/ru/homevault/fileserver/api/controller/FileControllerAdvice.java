package ru.homevault.fileserver.api.controller;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.homevault.fileserver.api.dto.ErrorResponse;
import ru.homevault.fileserver.core.exception.HomeVaultException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class FileControllerAdvice {

    @ExceptionHandler(HomeVaultException.class)
    public ResponseEntity<ErrorResponse> handleHomeVaultException(HomeVaultException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .error(ex.getMessage())
                .build();

        return ResponseEntity.status(ex.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable t) {
        log.error("Internal server error: {}", t.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error("Unexpected server error")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .error("Resource not found: " + ex.getResourcePath())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .error("Missing required request parameter: " + ex.getParameterName())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .stream()
                .map(v -> "%s".formatted(v.getMessage()))
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder().error(message).build());
    }

}
