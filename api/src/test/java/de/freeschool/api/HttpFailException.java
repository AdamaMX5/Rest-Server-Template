package de.freeschool.api;

import org.springframework.http.HttpStatusCode;

public class HttpFailException extends RuntimeException {

    private HttpStatusCode statusCode;

    public HttpFailException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
