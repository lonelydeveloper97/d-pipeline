package com.lonelydeveloper97.dpipeline.errors;

import com.lonelydeveloper97.dpipeline.Source;

public interface ErrorHandlingSource<T> extends Source<T>, ErrorHandling<ErrorHandlingSource<T>> {
}
