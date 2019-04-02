package com.lonelydeveloper97.dpipeline.pipe;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.pipe.immutability.ImmutablePipe;
import com.lonelydeveloper97.dpipeline.util.function.Function;

public interface Pipe<T> extends Sink<T>, Source<T> {
    static <T> Pipe<T> create(){
        return new PipeImpl<>();
    }

    static <T> Pipe<T> createImmutable(Function<T,T> copy){
        return new ImmutablePipe<>(copy);
    }

    @Override
    Pipe<T> subscribe(Sink<T> sink);
}
