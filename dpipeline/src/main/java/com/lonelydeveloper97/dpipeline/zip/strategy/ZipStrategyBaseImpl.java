package com.lonelydeveloper97.dpipeline.zip.strategy;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ZipStrategyBaseImpl<T, V> implements ZipStrategy<T, V> {
    private final List<T> values1 = new ArrayList<>();
    private final List<V> values2 = new ArrayList<>();

    @Override
    public List<T> values1() {
        return values1;
    }

    @Override
    public List<V> values2() {
        return values2;
    }

    @Nullable
    protected  <A> A get(List<A> list, int pos) {
        if (list.size() > pos) {
            return list.get(pos);
        } else {
            return null;
        }
    }
}
