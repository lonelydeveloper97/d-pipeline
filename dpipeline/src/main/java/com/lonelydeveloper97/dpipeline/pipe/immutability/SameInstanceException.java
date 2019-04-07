package com.lonelydeveloper97.dpipeline.pipe.immutability;

public class SameInstanceException extends IllegalStateException {
    public SameInstanceException() {
    }

    public SameInstanceException(String s) {
        super(s);
    }

    public SameInstanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SameInstanceException(Throwable cause) {
        super(cause);
    }
}
