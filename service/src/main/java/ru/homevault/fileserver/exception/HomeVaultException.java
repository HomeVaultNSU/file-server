package ru.homevault.fileserver.exception;

import org.springframework.http.HttpStatus;

public class HomeVaultException extends RuntimeException {

    HttpStatus httpStatus;


    public HomeVaultException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HomeVaultException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
