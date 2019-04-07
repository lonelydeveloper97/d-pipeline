package com.lonelydeveloper97.dpipeline;

import com.lonelydeveloper97.dpipeline.errors.ErrorHandlingSource;

public class ErrorHandlingPipelineImpl<T, V> implements Pipeline.ErrorHandlingPipeline<T, V> {
    private final ErrorHandlingSource<V> tail;
    private final Sink<T> head;

    ErrorHandlingPipelineImpl(Sink<T> head, ErrorHandlingSource<V> tail) {
        this.tail = tail;
        this.head = head;
    }

    @Override
    public Sink<T> head() {
        return head;
    }

    @Override
    public Source<V> tail() {
        return tail;
    }

    @Override
    public Pipeline<T, V> subscribe(Sink<V> sink) {
        tail.subscribe(sink);
        return this;
    }

    @Override
    public void unsubscribe(Sink<V> sink) {
        tail.unsubscribe(sink);
    }

    @Override
    public void clear() {
        tail.clear();
    }

    @Override
    public int size() {
        return tail.size();
    }

    @Override
    public ErrorHandlingSource<V> allowHandlingSuper(boolean allowed) {
        return tail.allowHandlingSuper(allowed);
    }

    @Override
    public void accept(T t) {
        head.accept(t);
    }
}
