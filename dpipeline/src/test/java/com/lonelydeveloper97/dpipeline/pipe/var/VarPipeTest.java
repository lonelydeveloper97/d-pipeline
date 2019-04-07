package com.lonelydeveloper97.dpipeline.pipe.var;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;

public class VarPipeTest {

    //Here is an example of main behavioral difference for VarPipe.
    //If You have subscribed on it after values were accepted it will give you only current value.
    @Test
    public void testSubscribeAfterAccept() {
        AtomicInteger called = new AtomicInteger();
        VarPipe<String> varPipe = VarPipe.create();

        varPipe.acceptAll(Arrays.asList("A", "B", "C", "D"));

        varPipe.subscribe(s -> called.incrementAndGet());

        assertEquals(1, called.get());
    }

    @Test
    public void testSubscribeBeforeAccept() {
        AtomicInteger called = new AtomicInteger();
        VarPipe<String> varPipe = VarPipe.create();

        varPipe.subscribe(s -> called.incrementAndGet());

        varPipe.acceptAll(Arrays.asList("A", "B", "C", "D"));

        assertEquals(4, called.get());
    }

}