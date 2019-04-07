package com.lonelydeveloper97.dpipeline;

class PipelineImpl<T, V> implements Pipeline<T, V> {

    private final Sink<T> head;
    private final Source<V> tail;

    PipelineImpl(Sink<T> head, Source<V> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public PipelineImpl<T, V> subscribe(Sink<V> sink) {
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
    public void accept(T t) {
        head.accept(t);
    }

    @Override
    public Sink<T> head() {
        return head;
    }

    @Override
    public Source<V> tail() {
        return tail;
    }
}