package com.lonelydeveloper97.dpipeline;

import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.errors.ErrorHandlingSource;
import com.lonelydeveloper97.dpipeline.errors.ErrorHandlingSourceDecorator;
import com.lonelydeveloper97.dpipeline.filtering.FilterableSource;
import com.lonelydeveloper97.dpipeline.lock.LockableSource;
import com.lonelydeveloper97.dpipeline.mapping.MappableSource;
import com.lonelydeveloper97.dpipeline.pipe.stream.StreamPipe;
import com.lonelydeveloper97.dpipeline.util.function.Function;
import com.lonelydeveloper97.dpipeline.util.function.Optional;
import com.lonelydeveloper97.dpipeline.zip.ZippableSource;

public interface Source<T> extends FilterableSource<T>, MappableSource<T>, CollectionSource<T>, ZippableSource<T>, LockableSource<T> {

    default Source<T> immute(Function<T, T> clone) {
        StreamPipe<T> streamPipe = StreamPipe.createImmutable(clone);
        this.subscribe(streamPipe);
        return streamPipe;
    }


    default Source<Optional<T>> ofOptional() {
        return createPipe((a, pipe) -> pipe.accept(Optional.ofNullable(a)));
    }

    default ErrorHandlingSource<T> addErrorHandling() {
        return new ErrorHandlingSourceDecorator<>(this);
    }

    default <E extends Throwable> ErrorHandlingSource<T> addErrorHandling(Class<E> eClass, Sink<E> errorSink) {
        return addErrorHandling().addErrorHandling(eClass, errorSink);
    }
}
