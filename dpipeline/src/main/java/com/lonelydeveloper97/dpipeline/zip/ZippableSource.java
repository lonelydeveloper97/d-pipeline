package com.lonelydeveloper97.dpipeline.zip;


import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.util.Pair;
import com.lonelydeveloper97.dpipeline.util.function.BiFunction;
import com.lonelydeveloper97.dpipeline.util.function.Optional;

public interface ZippableSource<T> extends SimpleSorce<T> {

    default <V, R> Source<R> zip(Source<V> source, ZipStrategy<T, V> zipStrategy, BiFunction<Optional<T>, Optional<V>, R> zipFunction) {
        Pipe<R> pipe = Pipe.create();
        zipStrategy.subscribe(p -> pipe.accept(zipFunction.apply(p.getFirst(), p.getSecond())));
        zipStrategy.apply(this, source);
        return pipe;
    }

    default <V> Source<Pair<T, V>> zip(Source<V> source, ZipStrategy<T, V> zipStrategy) {
        zipStrategy.apply(this, source);
        return zipStrategy;
    }

    default <V> Source<Pair<T, V>> zipEmptyAllowed(Source<V> source) {
        return zip(source, ZipStrategy.createEmptyAllowed());
    }

    default <V> Source<Pair<T, V>> zip(Source<V> source) {
        return zip(source, ZipStrategy.createDefault());
    }
}
