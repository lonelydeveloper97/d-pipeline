package com.lonelydeveloper97.dpipeline.errors;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;

import java.util.HashMap;
import java.util.Map;

public class ErrorHandlingSourceDecorator<T> implements ErrorHandlingSource<T> {
    private final Source<T> decorable;

    private final Map<Class<? extends Throwable>, Pipe<Throwable>> errorsMapping = new HashMap<>();
    private final Map<Sink<T>, Sink<T>> subscriptionMapping = new HashMap<>();

    public ErrorHandlingSourceDecorator(Source<T> decorable) {
        this.decorable = decorable;
    }

    public <E extends Throwable> void addErrorHandlingPipe(Class<E> eClass, Pipe<E> pipe) {
        errorsMapping.put(eClass, (Pipe<Throwable>) pipe);
    }

    @Override
    public ErrorHandlingSource<T> subscribe(Sink<T> sink) {
        Sink<T> sinkHandlerDecorator = t -> {
            try {
                sink.accept(t);
            } catch (Exception throwable) {
                Pipe<Throwable> pipe = errorsMapping.get(throwable.getClass());
                if (pipe != null) {
                    pipe.accept(throwable);
                } else {
                    throw throwable;
                }
            }
        };

        subscriptionMapping.put(sink, sinkHandlerDecorator);
        decorable.subscribe(sinkHandlerDecorator);
        return this;
    }

    @Override
    public void unsubscribe(Sink<T> sink) {
        decorable.unsubscribe(subscriptionMapping.get(sink));
    }

    @Override
    public void clear() {
        decorable.clear();
    }


    @Override
    public <E extends Throwable> ErrorHandlingSource<T> addErrorHandling(Class<E> eClass, Pipe<E> errorHandlingPile) {
        addErrorHandlingPipe(eClass, errorHandlingPile);
        return this;
    }
}
