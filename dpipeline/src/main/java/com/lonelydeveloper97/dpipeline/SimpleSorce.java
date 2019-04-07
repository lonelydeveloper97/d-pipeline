package com.lonelydeveloper97.dpipeline;

import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.pipe.stream.StreamPipe;
import com.lonelydeveloper97.dpipeline.util.function.BiConsumer;


public interface SimpleSorce<T> {
    Source<T> subscribe(Sink<T> sink);

    void unsubscribe(Sink<T> sink);

    void clear();

    int size();

    default <V> Pipe<V> createPipe() {
        return StreamPipe.create();
    }

    default <V> Pipe<V> createPipe(BiConsumer<T, Pipe<V>> fillFunction) {
        Pipe<V> vStreamPipe = createPipe();
        this.subscribe(t -> fillFunction.accept(t, vStreamPipe));
        return vStreamPipe;
    }
}
