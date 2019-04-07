package com.lonelydeveloper97.dpipeline.pipe.var;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;

import java.util.ArrayList;
import java.util.List;

public class VarPipeImpl<T> implements VarPipe<T> {
    private final List<Sink<T>> sinks = new ArrayList<>();
    private boolean initialized; //We allow nulls, but don'' allow passing them if they haven't come "outside"
    private T value = null;

    @Override
    public Pipe<T> subscribe(Sink<T> sink) {
        sinks.add(sink);
        if (initialized)
            sink.accept(value);
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
        value = t;
        for (Sink<T> sink : sinks) {
            sink.accept(t);
        }
    }
}
