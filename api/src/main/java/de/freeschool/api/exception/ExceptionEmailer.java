package de.freeschool.api.exception;


import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionEmailer {

    public static String emailExceptionMessage(String message, Exception exc) {
        // TODO send email to admin
        String stackTrace = "";
        if (exc != null) {
            stackTrace = "\n" + getStackTrace(exc);
        }
        System.err.println("emailExceptionMessage: \n" + message + stackTrace);
        return message;
    }

    public static void throwEmailExceptionMessage(String message, Exception exc) throws Exception {
        emailExceptionMessage(message, exc);
        if (exc == null) {
            throw new RuntimeException(message);
        } else {
            throw exc;
        }
    }

    public static String getStackTrace(Exception exc) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exc.printStackTrace(pw);
        return sw.toString();
    }
}
