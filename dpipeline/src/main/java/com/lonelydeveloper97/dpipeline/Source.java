package com.lonelydeveloper97.dpipeline;

import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.errors.ErrorHandlingSource;
import com.lonelydeveloper97.dpipeline.errors.ErrorHandlingSourceDecorator;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.pipe.PipeImpl;
import com.lonelydeveloper97.dpipeline.filtering.FilterableSource;
import com.lonelydeveloper97.dpipeline.pipe.immutability.ImmutablePipe;
import com.lonelydeveloper97.dpipeline.mapping.MappableSource;
import com.lonelydeveloper97.dpipeline.util.function.Function;
import com.lonelydeveloper97.dpipeline.util.function.Optional;
import com.lonelydeveloper97.dpipeline.zip.ZippableSource;

public interface Source<T> extends FilterableSource<T>, MappableSource<T>, CollectionSource<T>, ZippableSource<T> {

    default Source<T> immute(Function<T, T> clone) {
        Pipe<T> pipe = new ImmutablePipe<>(clone);
        this.subscribe(pipe);
        return pipe;
    }


    default Source<Optional<T>> ofOptional() {
        Pipe<Optional<T>> pipe = new PipeImpl<>();
        this.subscribe(a -> pipe.accept(Optional.ofNullable(a)));
        return pipe;
    }

    default ErrorHandlingSource<T> addErrorHandling(){
        return new ErrorHandlingSourceDecorator<>(this);
    }

    default <E extends Throwable> ErrorHandlingSource<T> addErrorHandling(Class<E> eClass, Pipe<E> errorHandlingPile){
        ErrorHandlingSourceDecorator<T> tSourceErrorsDecorator = new ErrorHandlingSourceDecorator<>(this);
        tSourceErrorsDecorator.addErrorHandling(eClass, errorHandlingPile);
        return tSourceErrorsDecorator;
    }

}
