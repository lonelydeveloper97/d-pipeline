package com.lonelydeveloper97.dpipeline.collections;

import com.lonelydeveloper97.dpipeline.Sink;

public interface Collector<T, R> extends Sink<T> {
    R collectCurrent();
}
