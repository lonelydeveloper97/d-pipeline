package com.lonelydeveloper97.dpipeline.errors;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.util.function.Optional;

import java.util.HashMap;
import java.util.Map;

public class ErrorHandlingSourceDecorator<T> implements ErrorHandlingSource<T> {
    private final Source<T> decorable;

    private final Map<Class<? extends Throwable>, Sink<? extends Throwable>> errorsMapping = new HashMap<>();
    private final Map<Sink<T>, Sink<T>> subscriptionMapping = new HashMap<>();
    private boolean superHandlingAllowed = false;


    public ErrorHandlingSourceDecorator(Source<T> decorable) {
        this.decorable = decorable;
        addErrorHandling(NoErrorHandlerException.class, e -> {
            throw e;
        });
    }


    @Override
    @SuppressWarnings("unchecked")
    public ErrorHandlingSource<T> subscribe(Sink<T> sink) {
        Sink<T> sinkHandlerDecorator = t -> {
            try {
                sink.accept(t);
            } catch (Throwable throwable) {
                getPipe(throwable.getClass())
                        .orElse(e -> getPipe(NoErrorHandlerException.class).get()
                                .accept(new NoErrorHandlerException(throwable))
                        )
                        .accept(throwable);
            }
        };

        subscriptionMapping.put(sink, sinkHandlerDecorator);
        decorable.subscribe(sinkHandlerDecorator);
        return this;
    }

    private Optional<Sink<Throwable>> getPipe(Class<? extends Throwable> classThrowable) {
        Optional<Sink<Throwable>> throwableSink = Optional.ofNullable((Sink<Throwable>) errorsMapping.get(classThrowable));
        if (!superHandlingAllowed) {
            return throwableSink;
        } else {
            Class<?> superclass = classThrowable.getSuperclass();
            return Optional.ofNullable(throwableSink.orElse(
                    Throwable.class.isAssignableFrom(superclass) ?
                            getPipe((Class<? extends Throwable>) superclass).orElse(null) :
                            null)
            );
        }
    }

    @Override
    public ErrorHandlingSource<T> allowHandlingSuper(boolean allowed) {
        superHandlingAllowed = allowed;
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
    public int size() {
        return decorable.size();
    }

    @Override
    public <E extends Throwable> ErrorHandlingSource<T> addErrorHandling(Class<E> eClass, Sink<E> errorSink) {
        errorsMapping.put(eClass, errorSink);
        return this;
    }
}
