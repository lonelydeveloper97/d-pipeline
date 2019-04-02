package com.lonelydeveloper97.dpipeline.zip;

import android.support.v4.util.Pair;

import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.collections.ListCollector;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ZippableSourceTest {

    @Test
    public void zip() {
        AtomicInteger called = new AtomicInteger(0);

        Pipe<String> pipeForStrings = Pipe.create();

        pipeForStrings.zip(CollectionSource.fromIterable(Arrays.asList(1, 2, 3, 4)),
                ZipStrategy.createDefault(),
                (stringOptional, integerOptional) -> stringOptional.orElse("").concat(integerOptional.map(Object::toString).orElse("")))
                .subscribe(createCheckPipe("A", "A1"))
                .subscribe(createCheckPipe("B", "B2"))
                .subscribe(createCheckPipe("C", "C3"))
                .subscribe(a -> called.incrementAndGet());

        pipeForStrings.accept("A");
        pipeForStrings.accept("B");
        pipeForStrings.accept("C");

        assertEquals(3, called.get());

        pipeForStrings.accept("D");
        pipeForStrings.accept("E");

        assertEquals(4, called.get());
    }

    private Pipe<String> createCheckPipe(String filter, String value) {
        Pipe<String> stringPipe = Pipe.create();
        stringPipe.filter(s -> s.startsWith(filter)).subscribe(a -> assertEquals(value, a));
        return stringPipe;
    }

    @Test
    public void zipEmptyAllowed() {
        AtomicBoolean called = new AtomicBoolean(false);

        Source<String> stringSource = CollectionSource.fromIterable(Arrays.asList("A", "B", "C", "D"));
        stringSource
                .zipEmptyAllowed(CollectionSource.fromIterable(Arrays.asList(1, 2)))
                .map(ListCollector.create(), p -> p.getFirst().get().equals("D"))
                .subscribe(l -> assertEquals("A", l.get(0).getFirst().get()))
                .subscribe(l -> assertEquals(1, (int) l.get(0).getSecond().get()))
                .subscribe(l -> assertEquals("D", l.get(3).getFirst().get()))
                .subscribe(l -> assertFalse(l.get(3).getSecond().isPresent()))
                .subscribe(l -> assertEquals(4, l.size()))
                .subscribe(l -> called.set(true));


        assertTrue(called.get());

    }


    @Test
    public void zipDefaultForPipe() {
        AtomicBoolean called = new AtomicBoolean(false);

        Pipe<String> pipeForStrings = Pipe.create();

        pipeForStrings.zip(CollectionSource.fromIterable(Arrays.asList(1, 2, 3)))
                .map(ListCollector.create(), p -> p.getFirst().orElse("").equals("C"))
                .subscribe(l -> assertEquals("A", l.get(0).getFirst().get()))
                .subscribe(l -> assertEquals(1, (int) l.get(0).getSecond().get()))
                .subscribe(l -> assertEquals("C", l.get(2).getFirst().get()))
                .subscribe(l -> assertEquals(3, l.size()))
                .subscribe(l -> called.set(true));

        pipeForStrings.accept("A");
        pipeForStrings.accept("B");
        pipeForStrings.accept("C");

        assertTrue(called.get());
    }
}