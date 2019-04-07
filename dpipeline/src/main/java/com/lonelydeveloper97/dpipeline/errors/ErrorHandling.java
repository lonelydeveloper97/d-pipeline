package com.lonelydeveloper97.dpipeline.errors;

public interface ErrorHandling<T> {
    T allowHandlingSuper(boolean allowed);
}
