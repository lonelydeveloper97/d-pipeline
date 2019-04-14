package com.lonelydeveloper97.dpipeline.zip;


import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.collections.Collector;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.pipe.var.VarPipe;
import com.lonelydeveloper97.dpipeline.util.Pair;
import com.lonelydeveloper97.dpipeline.zip.strategy.ZipStrategy;

import java.util.List;

public class ZippingSourceImpl<T, V> implements ZippingSource<T, V> {
    private final ZipStrategy<T, V> zipStrategy;

    private Pipe<Pair<T, V>> output = createPipe();

    ZippingSourceImpl(ZipStrategy<T, V> zipStrategy) {
        this.zipStrategy = zipStrategy;
    }

    @Override
    public void apply(CollectionSource<T> source1, CollectionSource<V> source2) {
        zipStrategy.values1().addAll(source1.collectAll(Collector.listCollector()));
        zipStrategy.values2().addAll(source2.collectAll(Collector.listCollector()));
        zipStrategy.zipCurrent(output);

        this.addZipSubscriber(source1, zipStrategy.values1());
        this.addZipSubscriber(source2, zipStrategy.values2());
    }

     private <A> void addZipSubscriber(CollectionSource<A> source, List<A> list){
         VarPipe<A> varPipe1 = VarPipe.create();
         source.subscribe(varPipe1);
         varPipe1.clear();
         varPipe1.subscribe(t -> {
             list.add(t);
             zipStrategy.sendToOut(list.size() - 1, output);
         });
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

    @Override
    public int size() {
        return output.size();
    }
}