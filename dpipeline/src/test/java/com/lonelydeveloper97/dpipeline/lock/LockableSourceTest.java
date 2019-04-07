package com.lonelydeveloper97.dpipeline.lock;

import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.pipe.stream.StreamPipe;
import com.lonelydeveloper97.dpipeline.pipe.var.VarPipe;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class LockableSourceTest {

    @Test
    public void addLockerForStreamPipe() {
        Pipe<String> pipe = StreamPipe.create();
        pipe.acceptAll(Arrays.asList("A", "B", "C", "D"));

        LockerImpl locker;
        AtomicInteger firstSubscriberCalled = new AtomicInteger();


        Source<String> lockedSource = pipe.addLocker(locker = Locker.createLocked());
        lockedSource.subscribe(a -> firstSubscriberCalled.incrementAndGet());
        assertEquals(0, firstSubscriberCalled.get());

        locker.unlock();
        assertEquals(4, firstSubscriberCalled.get());

        pipe.accept("E");
        assertEquals(5, firstSubscriberCalled.get());

        locker.lock();
        pipe.accept("F");
        pipe.accept("G");
        assertEquals(5, firstSubscriberCalled.get());

        locker.unlock();
        assertEquals(7, firstSubscriberCalled.get());


        AtomicInteger secondSubscriberCalled = new AtomicInteger();

        lockedSource.subscribe(l -> secondSubscriberCalled.incrementAndGet());
        assertEquals(7, secondSubscriberCalled.get());
    }


    @Test
    public void addLockerForVarPipe() {
        Pipe<String> pipe = VarPipe.create();
        pipe.acceptAll(Arrays.asList("A", "B", "C", "D"));

        LockerImpl locker;
        AtomicInteger called = new AtomicInteger();

        Source<String> lockedSource = pipe.addLocker(locker = Locker.createLocked());

        lockedSource.subscribe(a -> called.incrementAndGet());
        assertEquals(0, called.get());

        locker.unlock();
        assertEquals(1, called.get());

        pipe.accept("E");
        assertEquals(2, called.get());

        locker.lock();
        pipe.accept("F");
        pipe.accept("G");
        assertEquals(2, called.get());

        locker.unlock();
        assertEquals(3, called.get());


        AtomicInteger secondSubscriberCalled = new AtomicInteger();

        lockedSource.subscribe(l -> secondSubscriberCalled.incrementAndGet());
        assertEquals(1, secondSubscriberCalled.get());
    }

    @Test
    public void fireTest(){
        Pipe<String> pipe = VarPipe.create();
        pipe.acceptAll(Arrays.asList("A", "B", "C", "D"));

        LockerImpl locker;
        AtomicInteger called = new AtomicInteger();

        Source<String> lockedSource = pipe.addLocker(locker = Locker.createLocked());

        lockedSource.subscribe(a -> called.incrementAndGet());
        assertEquals(0, called.get());

        locker.fireCurrent();
        assertEquals(1, called.get());

        pipe.accept("E");
        pipe.accept("F");
        assertEquals(1, called.get());
    }
}