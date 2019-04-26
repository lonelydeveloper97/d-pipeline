package com.lonelydeveloper97.dpipeline.mapping;

import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.collections.Collector;
import com.lonelydeveloper97.dpipeline.pipe.stream.StreamPipe;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class MappableSourceTest {

    @Test
    public void map() {
        AtomicInteger called = new AtomicInteger();

        CollectionSource.fromIterable(Arrays.asList("1","2","3"))
                .map(Integer::parseInt)
                .collect(Collector.listCollector(), integer -> integer.equals(3))
                .subscribe(l -> assertEquals(3, l.size()))
                .subscribe(l -> assertEquals(3, (int) l.get(2)))
                .subscribe(l -> called.incrementAndGet());

        assertEquals(1, called.get());
    }

    @Test
    public void mapAsync() {
        AtomicInteger calledArr = new AtomicInteger();
        AtomicInteger calledInt = new AtomicInteger();

        CollectionSource.fromIterable(Arrays.asList("1","2","3"))
                .asyncMap(s -> {
                    StreamPipe<Integer> i = StreamPipe.create();
                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                            i.accept(Integer.parseInt(s));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).run();
                    return i;
                })
                .subscribe(i->calledInt.incrementAndGet())
                .collect(Collector.listCollector(), integer -> integer.equals(3))
                .subscribe(l -> assertEquals(3, l.size()))
                .subscribe(l -> assertEquals(3, (int) l.get(2)))
                .subscribe(l -> calledArr.incrementAndGet());


        new Thread(() -> {
            try {
                Thread.sleep(200);
                assertEquals(1, calledArr.get());
                assertEquals(3, calledInt.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).run();
    }

    @Test
    public void to() {
        //TODO
    }
}