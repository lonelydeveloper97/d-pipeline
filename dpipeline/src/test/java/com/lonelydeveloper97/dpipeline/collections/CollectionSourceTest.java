package com.lonelydeveloper97.dpipeline.collections;

import com.lonelydeveloper97.dpipeline.util.function.Optional;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    public void testCollectPredicate() {
        CollectionSource
                .fromIterable(Arrays.asList("1", "2", "3", null))
                .ofOptional()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(ListCollector.create(), t -> t.equals("3"))
                .subscribe(t -> assertEquals(3, t.size()));
    }

    @Test
    public void collect() {
        CollectionSource
                .fromIterable(Arrays.asList("1", "2", "3", null))
                .ofOptional()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(ListCollector.create(), 2)
                .subscribe(t -> assertEquals(2, t.size()));

    }

    @Test
    public void collectAll() {
        List<String> result = CollectionSource
                .fromIterable(Arrays.asList("1", "2", "3", "4", null))
                .ofOptional()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collectAll(ListCollector.create());

        assertEquals(4, result.size());

    }

    @Test
    public void split() {
        AtomicInteger called = new AtomicInteger();
        CollectionSource
                .fromIterable(Arrays.asList("1", "2", "3", "4", null))
                .ofOptional()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(ListCollector.create(), 2)
                .subscribe(t -> assertEquals(2, t.size()))
                .subscribe(l -> called.incrementAndGet());

        assertEquals(2, called.get());
    }
}