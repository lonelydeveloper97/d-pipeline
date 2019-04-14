package com.lonelydeveloper97.dpipeline.zip;

import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.collections.Collector;
import com.lonelydeveloper97.dpipeline.pipe.stream.StreamPipe;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ZippableSourceTest {

    @Test
    public void zip() {
        AtomicInteger called = new AtomicInteger(0);

        StreamPipe<String> streamPipeForStrings = StreamPipe.create();

        streamPipeForStrings.zip(CollectionSource.fromIterable(Arrays.asList(1, 2, 3, 4)),
                ZippingSource.createDefault(),
                (stringOptional, integerOptional) -> stringOptional.orElse("").concat(integerOptional.map(Object::toString).orElse("")))
                .subscribe(createCheckPipe("A", "A1"))
                .subscribe(createCheckPipe("B", "B2"))
                .subscribe(createCheckPipe("C", "C3"))
                .subscribe(a -> called.incrementAndGet());

        streamPipeForStrings.accept("A");
        streamPipeForStrings.accept("B");
        streamPipeForStrings.accept("C");

        assertEquals(3, called.get());

        streamPipeForStrings.accept("D");
        streamPipeForStrings.accept("E");

        assertEquals(4, called.get());
    }

    private StreamPipe<String> createCheckPipe(String filter, String value) {
        StreamPipe<String> stringStreamPipe = StreamPipe.create();
        stringStreamPipe.filter(s -> s.startsWith(filter)).subscribe(a -> assertEquals(value, a));
        return stringStreamPipe;
    }

    @Test
    public void zipEmptyAllowed() {
        AtomicBoolean called = new AtomicBoolean(false);

        Source<String> stringSource = CollectionSource.fromIterable(Arrays.asList("A", "B", "C", "D"));
        stringSource
                .zipEmptyAllowed(CollectionSource.fromIterable(Arrays.asList(1, 2)))
                .collect(Collector.listCollector(), 4)
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

        StreamPipe<String> streamPipeForStrings = StreamPipe.create();

        streamPipeForStrings.zip(CollectionSource.fromIterable(Arrays.asList(1, 2, 3)))
                .collect(Collector.listCollector(), p -> p.getFirst().orElse("").equals("C"))
                .subscribe(l -> assertEquals("A", l.get(0).getFirst().get()))
                .subscribe(l -> assertEquals(1, (int) l.get(0).getSecond().get()))
                .subscribe(l -> assertEquals("C", l.get(2).getFirst().get()))
                .subscribe(l -> assertEquals(3, l.size()))
                .subscribe(l -> called.set(true));

        streamPipeForStrings.accept("A");
        streamPipeForStrings.accept("B");
        streamPipeForStrings.accept("C");

        assertTrue(called.get());
    }
}