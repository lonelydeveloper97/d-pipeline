package com.lonelydeveloper97.dpipeline.pipe.stream;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.util.function.Function;

public interface StreamPipe<T> extends Pipe<T> {
    static <T> StreamPipe<T> create(){
        return new StreamPipeImpl<>();
    }

    static <T> StreamPipe<T> createImmutable(Function<T,T> copy){
        return new ImmutableStreamPipe<>(copy);
    }

    @Override
    StreamPipe<T> subscribe(Sink<T> sink);
}
