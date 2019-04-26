package com.lonelydeveloper97.dpipeline.filtering;

import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.util.function.Function;
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

    default Source<T> filter(Predicate<T> predicate, Sink<T> orDo) {
        return createPipe((a, pipe) -> {
            if (predicate.test(a))
                pipe.accept(a);
            else
                orDo.accept(a);
        });
    }

    default Source<T> filter(Predicate<T> predicate) {
        return filter(predicate, (t) -> {
        });
    }

    default Source<T> filterNot(Predicate<T> predicate, Supplier<T> defaultValue) {
        return filter(predicate.negate(), defaultValue);
    }

    default Source<T> filterNot(Predicate<T> predicate, Sink<T> orDo) {
        return filter(predicate.negate(), orDo);
    }

    default Source<T> filterNot(Predicate<T> predicate) {
        return filter(predicate.negate());
    }

    default Source<T> asyncFilter(Function<T, Source<Boolean>> asyncFilterFunction, Supplier<T> defaultValue) {
        return createPipe((t, pipe) -> asyncFilterFunction.apply(t).subscribe(b -> {
            if (b)
                pipe.accept(t);
            else
                pipe.accept(defaultValue.get());
        }));
    }

    default Source<T> asyncFilter(Function<T, Source<Boolean>> asyncFilterFunction, Sink<T> orDo) {
        return createPipe((t, pipe) -> asyncFilterFunction.apply(t).subscribe(b -> {
            if (b)
                pipe.accept(t);
            else
                orDo.accept(t);
        }));
    }

    default Source<T> asyncFilter(Function<T, Source<Boolean>> asyncFilterFunction) {
        return asyncFilter(asyncFilterFunction, t -> {
        });
    }

    default Source<T> asyncFilterNot(Function<T, Source<Boolean>> asyncFilterFunction, Supplier<T> defaultValue) {
        return asyncFilter(t -> asyncFilterFunction.apply(t).map(b -> !b), defaultValue);
    }

    default Source<T> asyncFilterNot(Function<T, Source<Boolean>> asyncFilterFunction, Sink<T> orDo) {
        return asyncFilter(t -> asyncFilterFunction.apply(t).map(b -> !b), orDo);
    }

    default Source<T> asyncFilterNot(Function<T, Source<Boolean>> asyncFilterFunction) {
        return asyncFilter(t -> asyncFilterFunction.apply(t).map(b -> !b));
    }

}
