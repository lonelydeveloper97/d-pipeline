package com.lonelydeveloper97.dpipeline;

public interface SimpleSorce<T> {
    Source<T> subscribe(Sink<T> sink);

    void unsubscribe(Sink<T> sink);

    void clear();
}
