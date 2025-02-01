package de.freeschool.api.scan;

import de.freeschool.api.exception.MessageException;
import de.freeschool.api.exception.NoRefreshableTokenException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        System.out.println("unhandled Exception: " + e.getMessage());
        e.printStackTrace();
        // send email to registered E-Mails in properties files
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("unexpected error: " + e.getCause() + ": " + e.getMessage());
    }


    @ExceptionHandler(MessageException.class)
    public ResponseEntity<String> handleRuntimeException(MessageException e) {
        System.out.println("MessageException:" + e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleRuntimeException(MethodArgumentNotValidException e) {
        System.out.println("MethodArgumentNotValidException:" + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchMessageException.class)
    public ResponseEntity<String> handleRuntimeException(NoSuchMessageException e) {
        System.out.println("Language-Message-Exception: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleRuntimeException(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("POST Mediatype has to be JSON");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleRuntimeException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JSON has a Syntax-Error:\n" + e.getMessage());
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleRuntimeException(NumberFormatException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Number-format is not correct (e.x: 12.34) :\n" + e.getMessage());
    }

    @ExceptionHandler(NoRefreshableTokenException.class)
    public ResponseEntity<String> handleRuntimeException(NoRefreshableTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: " + e.getMessage());
    }

}
