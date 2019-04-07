package com.lonelydeveloper97.dpipeline.errors;

public class NoErrorHandlerException extends IllegalStateException {
    public NoErrorHandlerException() {
    }

    public NoErrorHandlerException(String s) {
        super(s);
    }

    public NoErrorHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    NoErrorHandlerException(Throwable cause) {
        super(cause);
    }
}