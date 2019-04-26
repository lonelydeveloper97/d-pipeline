package com.lonelydeveloper97.dpipeline;

import com.lonelydeveloper97.dpipeline.collections.Collector;
import com.lonelydeveloper97.dpipeline.errors.ErrorHandlingSource;
import com.lonelydeveloper97.dpipeline.lock.LockerImpl;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.pipe.stream.StreamPipe;
import com.lonelydeveloper97.dpipeline.pipe.var.VarPipe;
import com.lonelydeveloper97.dpipeline.util.Pair;
import com.lonelydeveloper97.dpipeline.util.function.BiFunction;
import com.lonelydeveloper97.dpipeline.util.function.Function;
import com.lonelydeveloper97.dpipeline.util.function.Optional;
import com.lonelydeveloper97.dpipeline.util.function.Predicate;
import com.lonelydeveloper97.dpipeline.util.function.Supplier;
import com.lonelydeveloper97.dpipeline.zip.ZippingSource;

public interface Pipeline<T, V> extends Sink<T>, Source<V> {

    interface ErrorHandlingPipeline<T, V> extends Pipeline<T, V>, ErrorHandlingSource<V> {
        static <T, V> ErrorHandlingPipeline<T, V> create(Sink<T> head, ErrorHandlingSource<V> tail) {
            return new ErrorHandlingPipelineImpl<>(head, tail);
        }
    }

    static <T> Pipeline<T, T> create(Pipe<T> head) {
        return create(head, head);
    }

    static <T> Pipeline<T, T> createStream() {
        StreamPipe<T> streamPipe = StreamPipe.create();
        return create(streamPipe);
    }

    static <T> Pipeline<T, T> createVar() {
        VarPipe<T> varPipe = VarPipe.create();
        return create(varPipe);
    }

    static <T, V> Pipeline<T, V> create(Sink<T> head, Source<V> tail) {
        return new PipelineImpl<>(head, tail);
    }

    Sink<T> head();

    Source<V> tail();

    @Override
    Pipeline<T, V> subscribe(Sink<V> sink);

    @Override
    default Pipeline<T, V> filter(Predicate<V> predicate) {
        return create(head(), tail().filter(predicate));
    }

    @Override
    default <R> Pipeline<T, R> map(Function<V, R> map) {
        return create(head(), tail().map(map));
    }

    @Override
    default <R> Pipeline<T, R> asyncMap(Function<V, Source<R>> map) {
        return new PipelineImpl<>(head(), tail().asyncMap(map));
    }

    @Override
    default Pipeline<T, V> immute(Function<V, V> clone) {
        return create(head(), tail().immute(clone));
    }

    @Override
    default Pipeline<T, Optional<V>> ofOptional() {
        return create(head(), tail().ofOptional());
    }

    @Override
    default ErrorHandlingPipeline<T, V> addErrorHandling() {
        return ErrorHandlingPipeline.create(head(), tail().addErrorHandling());
    }

    @Override
    default <E extends Throwable> ErrorHandlingPipeline<T, V> addErrorHandling(Class<E> eClass, Sink<E> errorSink) {
        return ErrorHandlingPipeline.create(head(), tail().addErrorHandling(eClass, errorSink));
    }

    @Override
    default <R> Pipeline<T, R> collect(Collector<V, R> collector, Predicate<V> collectPredicate) {
        return create(head(), tail().collect(collector, collectPredicate));
    }

    @Override
    default Pipeline<T, V> filter(Predicate<V> predicate, Supplier<V> defaultValue) {
        return create(head(), tail().filter(predicate, defaultValue));
    }

    @Override
    default Pipeline<T, V> filter(Predicate<V> predicate, Sink<V> orDo) {
        return create(head(), tail().filter(predicate, orDo));
    }

    @Override
    default Pipeline<T, V> filterNot(Predicate<V> predicate, Supplier<V> defaultValue) {
        return create(head(), tail().filterNot(predicate, defaultValue));
    }

    @Override
    default Pipeline<T, V> filterNot(Predicate<V> predicate, Sink<V> orDo) {
        return create(head(), tail().filterNot(predicate, orDo));
    }

    @Override
    default Pipeline<T, V> filterNot(Predicate<V> predicate) {
        return create(head(), tail().filterNot(predicate));
    }

    @Override
    default <R> Pipeline<T, R> to(Function<SimpleSorce<V>, Source<R>> map) {
        return create(head(), tail().to(map));
    }

    @Override
    default <V1, R> Pipeline<T, R> zip(Source<V1> source, ZippingSource<V, V1> zippingSource, BiFunction<Optional<V>, Optional<V1>, R> zipFunction) {
        return create(head(), tail().zip(source, zippingSource, zipFunction));
    }

    @Override
    default <V1> Pipeline<T, Pair<V, V1>> zip(Source<V1> source, ZippingSource<V, V1> zippingSource) {
        return create(head(), tail().zip(source, zippingSource));
    }

    @Override
    default <V1> Pipeline<T, Pair<V, V1>> zipEmptyAllowed(Source<V1> source) {
        return create(head(), tail().zipEmptyAllowed(source));
    }

    @Override
    default <V1> Pipeline<T, Pair<V, V1>> zip(Source<V1> source) {
        return create(head(), tail().zip(source));
    }

    @Override
    default Pipeline<T, V> addLocker(LockerImpl locker) {
        return create(head(), tail().addLocker(locker));
    }

    @Override
    default <V1> Pipeline<T, V1> collect(Collector<V, V1> collector, int number) {
        return create(head(), tail().collect(collector, number));
    }

    @Override
    default <V1> V1 collectAll(Collector<V, V1> collector) {
        return tail().collectAll(collector);
    }
}
