package com.lonelydeveloper97.dpipeline.collections;

import com.lonelydeveloper97.dpipeline.util.function.Optional;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class CollectionSourceTest {

    @Test
    public void fromIterable() {
        AtomicReference<String> result = new AtomicReference<>("");

        CollectionSource
                .fromIterable(Arrays.asList("1", "2", "3", null))
                .ofOptional()
                .filter(Optional::isPresent, () -> Optional.of("4"))
                .map(Optional::get)
                .subscribe(s -> result.set(result.get() + s));

        assertEquals("1234", result.get());
    }


    @Test
    public void testMap() {
        CollectionSource
                .fromIterable(Arrays.asList("1", "2", "3", null))
                .ofOptional()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ListCollector.create(), t -> t.equals("3"))
                .subscribe(t -> assertEquals(3, t.size()));
    }
}