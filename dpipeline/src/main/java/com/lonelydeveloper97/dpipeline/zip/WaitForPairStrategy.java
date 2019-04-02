package com.lonelydeveloper97.dpipeline.zip;


import android.support.annotation.Nullable;

import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class WaitForPairStrategy<T, V> implements ZipStrategy<T, V> {
    private List<T> sourceOneValues = new ArrayList<>();
    private List<V> sourceTwoValues = new ArrayList<>();
    private Pipe<Pair<T,V>> output = Pipe.create();

    @Override
    public void apply(SimpleSorce<T> source1, SimpleSorce<V> source2) {
        initialMerge(source1, source2);
        source1.subscribe(new Sink<T>() {
            int counter = 0;

            @Override
            public void accept(T t) {
                counter++;
                if (counter > sourceOneValues.size()) {
                    sourceOneValues.add(t);
                    sendToOut(counter - 1);
                }
            }
        });
        source2.subscribe(new Sink<V>() {
            int counter = 0;

            @Override
            public void accept(V t) {
                counter++;
                if (counter > sourceTwoValues.size()) {
                    sourceTwoValues.add(t);
                    sendToOut(counter - 1);
                }
            }
        });
    }

    private void initialMerge(SimpleSorce<T> source1, SimpleSorce<V> source2) {
        Sink<T> add1 = sourceOneValues::add;
        source1.subscribe(add1);
        source1.unsubscribe(add1);
        Sink<V> add2 = sourceTwoValues::add;
        source2.subscribe(add2);
        source2.unsubscribe(add2);
        mergeCunrent();
    }

    private void mergeCunrent() {
        int size = sourceOneValues.size() > sourceTwoValues.size() ? sourceOneValues.size() : sourceTwoValues.size();
        for (int i = 0; i < size; i++) {
            sendToOut(i);
        }
    }

    private void sendToOut(int i) {
        if (i < sourceOneValues.size() && i < sourceTwoValues.size()) {
            output.accept(new Pair<>(get(sourceOneValues, i), get(sourceTwoValues, i)));
        }
    }

    @Nullable
    private <A> A get(List<A> list, int pos) {
        if (list.size() > pos) {
            return list.get(pos);
        } else {
            return null;
        }
    }


    @Override
    public Source<Pair<T, V>> subscribe(Sink<Pair<T, V>> sink) {
        output.subscribe(sink);
        return this;
    }

    @Override
    public void unsubscribe(Sink<Pair<T, V>> sink) {
        output.unsubscribe(sink);
    }

    @Override
    public void clear() {
        output.clear();
    }
}