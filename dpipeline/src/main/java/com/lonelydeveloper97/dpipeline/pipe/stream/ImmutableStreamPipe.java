package com.lonelydeveloper97.dpipeline.pipe.stream;

import com.lonelydeveloper97.dpipeline.BuildConfig;
import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.pipe.immutability.SameInstanceException;
import com.lonelydeveloper97.dpipeline.util.function.Function;

import java.util.ArrayList;
import java.util.List;

public class ImmutableStreamPipe<T> implements StreamPipe<T> {
    private final List<Sink<T>> sinks = new ArrayList<>();
    private final Function<T, T> create;
    private List<T> values = new ArrayList<>();

    ImmutableStreamPipe(Function<T, T> create) {
        this.create = create;
    }

    @Override
    public StreamPipe<T> subscribe(Sink<T> sink) {
        sinks.add(sink);
        for(T val: values){
            sink.accept(create.apply(val));
        }
        return this;
    }

    @Override
    public void unsubscribe(Sink<T> sink) {
        sinks.remove(sink);
    }

    @Override
    public void clear() {
        sinks.clear();
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public void accept(T t) {
        T copyLast = create.apply(t);
        if (copyLast == t && BuildConfig.DEBUG) {
            throw new SameInstanceException("Your function must create new instance!");
        }
        values.add(copyLast);

        for (Sink<T> sink : sinks) {
            sink.accept(create.apply(copyLast));
        }
    }
}

