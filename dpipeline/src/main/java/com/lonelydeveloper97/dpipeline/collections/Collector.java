package com.lonelydeveloper97.dpipeline.collections;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.util.function.Function;

public interface Collector<T, R> extends Sink<T> {
    R collectCurrent();

    void reset();

    int size();

    static <A> Collector<A, String> stringCollector(Function<A, String> toStringFunction) {
        return new Collector<A, String>() {
            String s = "";
            int size;

            @Override
            public String collectCurrent() {
                return s;
            }

            @Override
            public void reset() {
                s = "";
                size = 0;
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public void accept(A t) {
                s += toStringFunction.apply(t);
                size += 1;
            }
        };
    }

    static <A> Collector<A, String> stringCollector() {
        return stringCollector(Object::toString);
    }

    static <A> ListCollector<A> listCollector() {
        return ListCollector.create();
    }
}
