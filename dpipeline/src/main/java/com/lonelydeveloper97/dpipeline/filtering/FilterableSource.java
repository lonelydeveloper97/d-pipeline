package com.lonelydeveloper97.dpipeline.filtering;

import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.pipe.PipeImpl;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.util.function.Predicate;
import com.lonelydeveloper97.dpipeline.util.function.Supplier;

public interface FilterableSource<T> extends SimpleSorce<T> {
    default Source<T> filter(Predicate<T> predicate, Supplier<T> defaultValue) {
        Pipe<T> pipe = new PipeImpl<>();
        this.subscribe(a -> {
            if (predicate.test(a))
                pipe.accept(a);
            else
                pipe.accept(defaultValue.get());
        });
        return pipe;
    }

    default Source<T> filter(Predicate<T> predicate, Runnable orDo) {
        Pipe<T> pipe = new PipeImpl<>();
        this.subscribe(a -> {
            if (predicate.test(a))
                pipe.accept(a);
            else orDo.run();
        });
        return pipe;
    }

    default Source<T> filter(Predicate<T> predicate) {
        return filter(predicate, () -> {
        });
    }

    default Source<T> filterNot(Predicate<T> predicate, Supplier<T> defaultValue) {
        return filter(predicate.negate(), defaultValue);
    }

    default Source<T> filterNot(Predicate<T> predicate, Runnable orDo) {
        return filter(predicate.negate(), orDo);
    }

    default Source<T> filterNot(Predicate<T> predicate) {
        return filter(predicate.negate());
    }
}
