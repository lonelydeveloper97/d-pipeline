package com.lonelydeveloper97.dpipeline.pipe;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.Source;

public interface Pipe<T> extends Source<T>, Sink<T> {
}
