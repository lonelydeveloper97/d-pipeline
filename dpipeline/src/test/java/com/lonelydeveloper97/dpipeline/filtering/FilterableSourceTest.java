package com.lonelydeveloper97.dpipeline.filtering;

import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.collections.Collector;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class FilterableSourceTest {

    @Test
    public void filter() {
        Collector<String, List<String>> collector = Collector.listCollector();

        CollectionSource.fromIterable(Arrays.asList("1", "2", "3"))
                .filter(s -> s.equals("3"))
                .subscribe(collector);

        assertEquals(1, collector.collectCurrent().size());
    }

    @Test
    public void filterWithDefault() {
        Collector<String, List<String>> collector = Collector.listCollector();

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
        Collector<String, List<String>> collector = Collector.listCollector();

        CollectionSource.fromIterable(Arrays.asList("1", "2", "3"))
                .filterNot(s -> s.equals("3"))
                .subscribe(collector);

        assertEquals(2, collector.collectCurrent().size());
    }

    @Test
    public void filterNotWithDefault() {
        Collector<String, List<String>> collector = Collector.listCollector();

        CollectionSource.fromIterable(Arrays.asList("1", "2", "3"))
                .filterNot(s -> s.equals("3"), () -> "4")
                .subscribe(collector);

        assertEquals("4", collector.collectCurrent().get(2));
    }

    @Test
    public void filterNotWithDo() {
        AtomicInteger doCalled = new AtomicInteger(0);
        Collector<String, List<String>> sink = Collector.listCollector();

        CollectionSource.fromIterable(Arrays.asList("1", "2", "3"))
                .filterNot(s -> s.equals("3"), () -> doCalled.set(doCalled.get() + 1))
                .subscribe(sink);

        assertEquals(1, doCalled.get());
    }
}