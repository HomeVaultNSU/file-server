package ru.homevault.fileserver.controller;


import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ru.homevault.fileserver.dto.ErrorResponse;
import ru.homevault.fileserver.exception.HomeVaultException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class FileControllerAdvice {

    @ExceptionHandler(HomeVaultException.class)
    public ResponseEntity<ErrorResponse> handleHomeVaultException(HomeVaultException ex) {
        HttpStatus status = determineStatus(ex);
        return new ResponseEntity<>(ErrorResponse.builder()
                .error(ex.getMessage())
                .build(), status);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable t) {
        return new ResponseEntity<>(ErrorResponse.builder().error("Unexpected server error").build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private HttpStatus determineStatus(HomeVaultException ex) {
        String message = ex.getMessage().toLowerCase();

        if (message.contains("not found")) {
            return HttpStatus.NOT_FOUND;
        } else if (message.contains("invalid") || message.contains("bad request")) {
            return HttpStatus.BAD_REQUEST;
        } else if (message.contains("permission") || message.contains("access")) {
            return HttpStatus.FORBIDDEN;
        } else if (message.contains("conflict") || message.contains("already exists")) {
            return HttpStatus.CONFLICT;
        } else if (message.contains("unsupported") || message.contains("not implemented")) {
            return HttpStatus.NOT_IMPLEMENTED;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
