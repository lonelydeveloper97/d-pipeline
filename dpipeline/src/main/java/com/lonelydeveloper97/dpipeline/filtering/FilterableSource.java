package com.lonelydeveloper97.dpipeline.filtering;

import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.util.function.Predicate;
import com.lonelydeveloper97.dpipeline.util.function.Supplier;

public interface FilterableSource<T> extends SimpleSorce<T> {
    default Source<T> filter(Predicate<T> predicate, Supplier<T> defaultValue) {
        return createPipe((a, pipe) -> {
            if (predicate.test(a))
                pipe.accept(a);
            else
                pipe.accept(defaultValue.get());
        });
    }

    default Source<T> filter(Predicate<T> predicate, Runnable orDo) {
        return createPipe((a, pipe) -> {
            if (predicate.test(a))
                pipe.accept(a);
            else
                orDo.run();
        });
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
