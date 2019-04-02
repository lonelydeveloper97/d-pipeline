package com.lonelydeveloper97.dpipeline.errors;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.Source;

public interface ErrorHandlingSource<T> extends Source<T> {
    @Override
    ErrorHandlingSource<T> subscribe(Sink<T> sink);
}
