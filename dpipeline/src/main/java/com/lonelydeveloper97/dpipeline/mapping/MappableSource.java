package com.lonelydeveloper97.dpipeline.mapping;

import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.pipe.PipeImpl;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.util.function.Function;

public interface MappableSource<T> extends SimpleSorce<T> {
    default <R> Source<R> map(Function<T, R> map) {
        Pipe<R> pipe = new PipeImpl<>();
        this.subscribe(a -> pipe.accept(map.apply(a)));
        return pipe;
    }

    default <R> Source<R> mapAsync(Function<T, Source<R>> map) {
        Pipe<R> pipe = new PipeImpl<>();
        this.subscribe((a -> map.apply(a).subscribe(pipe)));
        return pipe;
    }

    default <R> Source<R> to(Function<SimpleSorce<T>, Source<R>> map) {
        return map.apply(this);
    }
}
