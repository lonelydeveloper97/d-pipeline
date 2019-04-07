package com.lonelydeveloper97.dpipeline.zip.strategy;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.util.Pair;

import java.util.List;

public interface ZipStrategy<T, V> {
    List<T> values1();

    List<V> values2();

    void zipCurrent(Sink<Pair<T, V>> output);

    void sendToOut(int index, Sink<Pair<T, V>> output);
}
