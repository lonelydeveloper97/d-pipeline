package com.lonelydeveloper97.dpipeline.filtering;

import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.collections.ListCollector;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class FilterableSourceTest {

    @Test
    public void filter() {
        ListCollector<String> collector = ListCollector.create();

        CollectionSource.fromIterable(Arrays.asList("1", "2", "3"))
                .filter(s -> s.equals("3"))
                .subscribe(collector);

        assertEquals(1, collector.collectCurrent().size());
    }

    @Test
    public void filterWithDefault() {
        ListCollector<String> collector = ListCollector.create();

        CollectionSource.fromIterable(Arrays.asList("1", "2", "3"))
                .filter(s -> s.equals("3"), () -> "4")
                .subscribe(collector);


        assertEquals("4", collector.collectCurrent().get(1));
        assertEquals("4", collector.collectCurrent().get(0));
    }

    @Test
    public void filterWithDo() {
        AtomicInteger doCalled = new AtomicInteger(0);

        CollectionSource.fromIterable(Arrays.asList("1", "2", "3"))
                .filter(s -> s.equals("3"), () -> doCalled.set(doCalled.get() + 1));

        assertEquals(2, doCalled.get());
    }

    @Test
    public void filterNot() {
        ListCollector<String> collector = ListCollector.create();

        CollectionSource.fromIterable(Arrays.asList("1", "2", "3"))
                .filterNot(s -> s.equals("3"))
                .subscribe(collector);

        assertEquals(2, collector.collectCurrent().size());
    }

    @Test
    public void filterNotWithDefault() {
        ListCollector<String> collector = ListCollector.create();

        CollectionSource.fromIterable(Arrays.asList("1", "2", "3"))
                .filterNot(s -> s.equals("3"), () -> "4")
                .subscribe(collector);

        assertEquals("4", collector.collectCurrent().get(2));
    }

    @Test
    public void filterNotWithDo() {
        AtomicInteger doCalled = new AtomicInteger(0);
        ListCollector<String> sink = ListCollector.create();

        CollectionSource.fromIterable(Arrays.asList("1", "2", "3"))
                .filterNot(s -> s.equals("3"), () -> doCalled.set(doCalled.get() + 1))
                .subscribe(sink);

        assertEquals(1, doCalled.get());
    }
}