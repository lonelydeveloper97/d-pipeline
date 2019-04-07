package com.lonelydeveloper97.dpipeline.pipe.var;

import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.util.function.Function;

public interface VarPipe<T> extends Pipe<T> {

    static <T> VarPipe<T> create(){
        return new VarPipeImpl<>();
    }


    static <T> VarPipe<T> createImmutable(Function<T, T> clone){
        return new ImmutableVarPipe<>(clone);
    }


    default Source<T> immute(Function<T, T> clone) {
        Pipe<T> streamPipe = VarPipe.createImmutable(clone);
        this.subscribe(streamPipe);
        return streamPipe;
    }

    @Override
    default <V> Pipe<V> createPipe() {
        return create();
    }
}
