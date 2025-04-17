package ru.homevault.fileserver.exception;

public class HomeVaultException extends RuntimeException {
    public HomeVaultException(String message) {
        super(message);
    }

    public HomeVaultException(String message, Throwable cause) {
        super(message, cause);
    }
}
