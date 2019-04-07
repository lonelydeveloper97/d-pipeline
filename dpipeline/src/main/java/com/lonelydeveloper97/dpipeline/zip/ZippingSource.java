package com.lonelydeveloper97.dpipeline.zip;


import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.util.Pair;
import com.lonelydeveloper97.dpipeline.zip.strategy.DefaultStrategy;
import com.lonelydeveloper97.dpipeline.zip.strategy.EmptyAllowedStrategy;


public interface ZippingSource<T, V> extends Source<Pair<T, V>> {
    void apply(CollectionSource<T> source1, CollectionSource<V> source2);

    static <T, V> ZippingSource<T, V> createDefault() {
        return new ZippingSourceImpl<>(new DefaultStrategy<>());
    }

    static <T, V> ZippingSource<T, V> createEmptyAllowed() {
        return new ZippingSourceImpl<>(new EmptyAllowedStrategy<>());
    }
}
