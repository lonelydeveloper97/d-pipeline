package com.lonelydeveloper97.dpipeline.zip.strategy;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.util.Pair;

public class EmptyAllowedStrategy<T, V> extends ZipStrategyBaseImpl<T, V> {
    @Override
    public void sendToOut(int index, Sink<Pair<T, V>> output) {
        output.accept(new Pair<>(get(values1(), index), get(values2(), index)));
    }

    @Override
    public void zipCurrent(Sink<Pair<T, V>> output) {
        int size = values1().size() > values2().size() ? values1().size() : values2().size();
        for (int i = 0; i < size; i++) {
            sendToOut(i, output);
        }
    }
}
