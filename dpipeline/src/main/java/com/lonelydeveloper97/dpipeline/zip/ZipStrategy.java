package com.lonelydeveloper97.dpipeline.zip;


import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.util.Pair;
import com.lonelydeveloper97.dpipeline.util.function.Optional;

public interface ZipStrategy<T, V> extends Source<Pair<T, V>> {
    void apply(SimpleSorce<T> source1, SimpleSorce<V> source2);

    static <T, V> ZipStrategy<T, V> createDefault() {
        return new WaitForPairStrategy<>();
    }

    static <T, V> ZipStrategy<T, V> createEmptyAllowed() {
        return new EmptyAllowedStrategy<>();
    }
}
