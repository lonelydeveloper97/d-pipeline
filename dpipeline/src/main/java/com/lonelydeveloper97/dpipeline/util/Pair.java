package com.lonelydeveloper97.dpipeline.util;

import android.support.annotation.Nullable;

import com.lonelydeveloper97.dpipeline.util.function.Optional;

public class Pair<T,V> {
    private Optional<T> first;
    private Optional<V> second;

    public Pair(@Nullable T first, @Nullable V second) {
        this.first = Optional.ofNullable(first);
        this.second = Optional.ofNullable(second);
    }

    public Optional<T> getFirst() {
        return first;
    }

    public Optional<V> getSecond() {
        return second;
    }
}
