package com.lonelydeveloper97.dpipeline.collections;

import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.util.function.Predicate;

public interface CollectionSource<T> extends SimpleSorce<T> {

    static <T> Source<T> fromIterable(Iterable<T> iterable) {
        Pipe<T> pipe = Pipe.create();
        pipe.acceptAll(iterable);
        return pipe;
    }

    static <T> Source<T> fromSingle(T t) {
        Pipe<T> pipe = Pipe.create();
        pipe.accept(t);
        return pipe;
    }

    default <V> Source<V> map(Collector<T,V> collector, Predicate<T> collectPredicate){
        Pipe<V> pipe = Pipe.create();
        this.subscribe(t->{
            collector.accept(t);
            if(collectPredicate.test(t)){
                pipe.accept(collector.collectCurrent());
            }
        });
        return pipe;
    }


}
