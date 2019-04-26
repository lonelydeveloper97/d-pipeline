package com.lonelydeveloper97.dpipeline.mapping;

import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.util.function.Function;

public interface MappableSource<T> extends SimpleSorce<T> {
    default <R> Source<R> map(Function<T, R> map) {
        return createPipe((a, pipe) -> pipe.accept(map.apply(a)));
    }

    default <R> Source<R> asyncMap(Function<T, Source<R>> map) {
        return createPipe((a, pipe) -> map.apply(a).subscribe(pipe));
    }

    default <R> Source<R> to(Function<SimpleSorce<T>, Source<R>> map) {
        return map.apply(this);
    }
}
