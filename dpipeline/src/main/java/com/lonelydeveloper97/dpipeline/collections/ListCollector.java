package com.lonelydeveloper97.dpipeline.collections;

import java.util.ArrayList;
import java.util.List;

public interface ListCollector<T> extends Collector<T, List<T>> {

    static <T>ListCollector<T> create(){
        return new ListCollector<T>(){
            @Override
            public List<T> collectCurrent() {
                return accumulated;
            }

            List<T> accumulated = new ArrayList<>();

            @Override
            public void accept(T o) {
                accumulated.add(o);
            }
        };
    }
}