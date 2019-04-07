package com.lonelydeveloper97.dpipeline.pipe.stream;

import com.lonelydeveloper97.dpipeline.Sink;

import java.util.ArrayList;
import java.util.List;

public class StreamPipeImpl<T> implements StreamPipe<T> {
    private final List<Sink<T>> sinks = new ArrayList<>();
    private List<T> values = new ArrayList<>();
    
    @Override
    public StreamPipe<T> subscribe(Sink<T> sink) {
        sinks.add(sink);
        for (T val: values){
            sink.accept(val);
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
        values.clear();
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public void accept(T t) {
        values.add(t);
        for(Sink<T> sink: sinks){
            sink.accept(t);
        }
    }
}
