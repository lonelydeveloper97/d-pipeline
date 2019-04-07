package com.lonelydeveloper97.dpipeline.collections;

import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.pipe.stream.StreamPipe;
import com.lonelydeveloper97.dpipeline.util.function.Predicate;

public interface CollectionSource<T> extends SimpleSorce<T> {

    static <T> Source<T> fromIterable(Iterable<T> iterable) {
        StreamPipe<T> streamPipe = StreamPipe.create();
        streamPipe.acceptAll(iterable);
        return streamPipe;
    }

    static <T> Source<T> fromSingle(T t) {
        StreamPipe<T> streamPipe = StreamPipe.create();
        streamPipe.accept(t);
        return streamPipe;
    }

    default <V> Source<V> collect(Collector<T, V> collector, Predicate<T> collectPredicate) {
        return createPipe((a, pipe) -> {
            collector.accept(a);
            if (collectPredicate.test(a)) {
                pipe.accept(collector.collectCurrent());
                collector.reset();
            }
        });
    }

    default <V> Source<V> collect(Collector<T, V> collector, int number) {
        return createPipe((a, pipe) -> {
            collector.accept(a);
            if (collector.size() == number) {
                pipe.accept(collector.collectCurrent());
                collector.reset();
            }
        });
    }

    default <V> V collectAll(Collector<T, V> collector) {
        this.subscribe(collector);
        this.unsubscribe(collector);
        return collector.collectCurrent();
    }
}
