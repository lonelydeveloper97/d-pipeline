package com.lonelydeveloper97.dpipeline.zip;


import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.util.Pair;
import com.lonelydeveloper97.dpipeline.util.function.BiFunction;
import com.lonelydeveloper97.dpipeline.util.function.Optional;

public interface ZippableSource<T> extends CollectionSource<T> {

    default <V, R> Source<R> zip(Source<V> source, ZippingSource<T, V> zippingSource, BiFunction<Optional<T>, Optional<V>, R> zipFunction) {
        Pipe<R> streamPipe = createPipe();
        zippingSource.subscribe(p -> streamPipe.accept(zipFunction.apply(p.getFirst(), p.getSecond())));
        zippingSource.apply(this, source);
        return streamPipe;
    }

    default <V> Source<Pair<T, V>> zip(Source<V> source, ZippingSource<T, V> zippingSource) {
        zippingSource.apply(this, source);
        return zippingSource;
    }

    default <V> Source<Pair<T, V>> zipEmptyAllowed(Source<V> source) {
        return zip(source, ZippingSource.createEmptyAllowed());
    }

    default <V> Source<Pair<T, V>> zip(Source<V> source) {
        return zip(source, ZippingSource.createDefault());
    }
}
