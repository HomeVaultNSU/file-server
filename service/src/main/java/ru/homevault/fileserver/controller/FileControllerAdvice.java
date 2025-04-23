package ru.homevault.fileserver.controller;


import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.homevault.fileserver.dto.ErrorResponse;
import ru.homevault.fileserver.exception.HomeVaultException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class FileControllerAdvice {

    @ExceptionHandler(HomeVaultException.class)
    public ResponseEntity<ErrorResponse> handleHomeVaultException(HomeVaultException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .error(ex.getMessage())
                .build(), ex.getHttpStatus());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable t) {
        return new ResponseEntity<>(ErrorResponse.builder().error("Unexpected server error").build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
