package de.freeschool.api.exception;

public class NotAuthorizedException extends Exception {

    public NotAuthorizedException() {
        super("login or register first.");
    }
}
