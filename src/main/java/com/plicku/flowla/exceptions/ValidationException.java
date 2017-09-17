package com.plicku.flowla.exceptions;

public class ValidationException extends Exception {
    public ValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ValidationException(String s) {
    }
}
