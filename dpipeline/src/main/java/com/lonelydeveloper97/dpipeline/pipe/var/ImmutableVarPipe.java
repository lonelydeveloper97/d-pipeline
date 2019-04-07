package com.lonelydeveloper97.dpipeline.pipe.var;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.util.function.Function;

import java.util.ArrayList;
import java.util.List;

public class ImmutableVarPipe<T> implements VarPipe<T> {
    private final List<Sink<T>> sinks = new ArrayList<>();
    private final Function<T, T> create;

    private boolean initialized; //We allow nulls, but don'' allow passing them if they haven't come "outside"
    private T value = null;

    ImmutableVarPipe(Function<T, T> create) {
        this.create = create;
    }

    @Override
    public Pipe<T> subscribe(Sink<T> sink) {
        sinks.add(sink);
        if (initialized)
            sink.accept(create.apply(value));
        return this;
    }

    @Override
    public void unsubscribe(Sink<T> sink) {
        sinks.remove(sink);
    }

    @Override
    public void clear() {
        sinks.clear();
        value = null;
        initialized = false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void accept(T t) {
        if (!initialized) {
            initialized = true;
        }
        value = create.apply(t);
        for (Sink<T> sink : sinks) {
            sink.accept(create.apply(t));
        }
    }
}