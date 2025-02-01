package de.freeschool.api.exception;

public class NoRefreshableTokenException extends RuntimeException {

    public NoRefreshableTokenException(String message) {
        super(message);
    }
}
