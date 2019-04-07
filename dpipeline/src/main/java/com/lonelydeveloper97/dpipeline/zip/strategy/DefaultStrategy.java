package com.lonelydeveloper97.dpipeline.zip.strategy;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.util.Pair;

public class DefaultStrategy<T, V> extends ZipStrategyBaseImpl<T, V> {
    @Override
    public void zipCurrent(Sink<Pair<T, V>> output) {
        int size = values1().size() > values2().size() ? values1().size() : values2().size();
        for (int i = 0; i < size; i++) {
            sendToOut(i, output);
        }
    }

    @Override
    public void sendToOut(int i, Sink<Pair<T, V>> output) {
        if (i < values1().size() && i < values2().size()) {
            output.accept(new Pair<>(get(values1(), i), get(values2(), i)));
        }
    }
}
