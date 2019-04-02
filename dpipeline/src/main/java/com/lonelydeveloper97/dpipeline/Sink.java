package com.lonelydeveloper97.dpipeline;


import com.lonelydeveloper97.dpipeline.util.function.Consumer;

public interface Sink<T> extends Consumer<T> {
    default void acceptAll(Iterable<T> iterable){
        for(T t: iterable){
            accept(t);
        }
    }
}