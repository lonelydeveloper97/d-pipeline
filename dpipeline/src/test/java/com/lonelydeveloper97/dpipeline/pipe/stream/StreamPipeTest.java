package com.lonelydeveloper97.dpipeline.pipe.stream;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;

public class StreamPipeTest {

    @Test
    public void testSubscribeAfterAccept() {
        AtomicInteger called = new AtomicInteger();
        StreamPipe<String> stringStreamPipe = StreamPipe.create();

        stringStreamPipe.acceptAll(Arrays.asList("A", "B", "C", "D"));

        stringStreamPipe.subscribe(s -> called.incrementAndGet());

        assertEquals(4, called.get());
    }

    @Test
    public void testSubscribeBeforeAccept() {
        AtomicInteger called = new AtomicInteger();
        StreamPipe<String> stringStreamPipe = StreamPipe.create();

        stringStreamPipe.subscribe(s -> called.incrementAndGet());

        stringStreamPipe.acceptAll(Arrays.asList("A", "B", "C", "D"));

        assertEquals(4, called.get());
    }


}