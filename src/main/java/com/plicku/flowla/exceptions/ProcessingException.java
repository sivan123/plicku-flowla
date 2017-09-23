package com.plicku.flowla.exceptions;

public class ProcessingException extends Exception {
    public ProcessingException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ProcessingException(String s) {
    }
}
