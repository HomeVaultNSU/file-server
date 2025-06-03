package ru.homevault.authserver.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HomeVaultException extends RuntimeException {

    private final HttpStatus httpStatus;

    public HomeVaultException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
