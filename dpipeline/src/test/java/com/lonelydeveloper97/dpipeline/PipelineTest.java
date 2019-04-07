package com.lonelydeveloper97.dpipeline;

import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.collections.Collector;
import com.lonelydeveloper97.dpipeline.collections.ListCollector;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PipelineTest {

    //Very complex test. Here we create tree of pipelines.
    //The Root - is simple string pipeline. We attach first "branch" to it
    @Test
    public void pipelineTest() {
        AtomicInteger called = new AtomicInteger();

        Pipeline.<String>createVar()
                .subscribe(Pipeline.<String>createVar()
                        .collect(ListCollector.create(), 2)
                        .map(l -> l.get(0) + l.get(1))
                        .zip(CollectionSource.fromIterable(Arrays.asList("!", "?", ")")))
                        .map(pair -> pair.getFirst().get() + pair.getSecond().get())
                        .subscribe(s -> assertTrue(s.equals("AABV!") || s.equals("CCa?") || s.equals("1)")))
                        .subscribe(s -> called.incrementAndGet())
                )
                .filterNot(String::isEmpty)
                .subscribe(Pipeline.<String>createVar()
                        .collect(ListCollector.create(), 4)
                        .subscribe(l -> assertEquals("a", l.get(l.size() - 1)))
                        .subscribe(l -> called.incrementAndGet())
                )
                .subscribe(Pipeline.<String>createVar()
                        .collect(ListCollector.create(), 2)
                        .immute(ArrayList::new)
                        .subscribe(Pipeline.<List<String>>createVar()
                                .map(l -> {
                                    l.add("1");
                                    return l;
                                })
                                .map(l -> CollectionSource.fromIterable(l).collectAll(Collector.stringCollector(s -> s)))
                                .subscribe(s -> assertTrue(s.equals("AABV1") || s.equals("CCa1") || s.equals("1$$1")))
                                .subscribe(l -> called.incrementAndGet())
                        )
                        .map(l -> CollectionSource.fromIterable(l).collectAll(Collector.stringCollector(s -> s)))
                        .zip(CollectionSource.fromIterable(Arrays.asList("!", "?", ")")))
                        .map(pair -> pair.getFirst().get() + pair.getSecond().get())
                        .subscribe(s -> assertTrue(s.equals("AABV!") || s.equals("CCa?") || s.equals("1$$)")))
                        .subscribe(s -> called.incrementAndGet())
                )
                .filter(s -> s.length() < 2)
                .subscribe(Pipeline.<String>createVar()
                        .addErrorHandling(NumberFormatException.class, Pipeline.<NumberFormatException>createVar()
                                .map(Exception::getMessage)
                                .subscribe(m -> assertTrue(m.contains("a"))))
                        .map(Integer::parseInt)
                        .subscribe(integer -> assertEquals(1, 1))
                        .subscribe(i -> called.incrementAndGet())
                )
                .acceptAll(Arrays.asList("AA", "BV", "CC", "a", "1", "", "$$"));

        assertEquals(11, called.get());
    }

}