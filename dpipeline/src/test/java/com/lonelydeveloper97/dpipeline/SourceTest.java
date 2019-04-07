package com.lonelydeveloper97.dpipeline;

import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.errors.NoErrorHandlerException;
import com.lonelydeveloper97.dpipeline.pipe.immutability.SameInstanceException;
import com.lonelydeveloper97.dpipeline.pipe.stream.StreamPipe;
import com.lonelydeveloper97.dpipeline.util.function.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SourceTest {

    @Test
    public void testImmute() {
        StreamPipe<MyMutableClass> streamPipe = StreamPipe.create();

        MyMutableClass mm = new MyMutableClass(10);

        //Assert instances are the same for mutable
        streamPipe.subscribe(a -> assertEquals(a, mm));

        //Assert that instance was mutated
        streamPipe.subscribe(a -> a.anInt = 1)
                .subscribe(a -> assertEquals(1, a.anInt));


        //Assert that instances are different but has the same value
        Source<MyMutableClass> imutableSource = streamPipe.immute(a -> new MyMutableClass(a.anInt))
                .subscribe(i -> assertNotEquals(mm, i))
                .subscribe(i -> assertEquals(1, i.anInt));

        //Assert that two different subscribers have different instances of class
        //IMO, this is pretty interesting example, pay attention to it
        Sink<MyMutableClass> saveAndCheckNext = new Sink<MyMutableClass>() {
            MyMutableClass myMutableClass;

            @Override
            public void accept(MyMutableClass myMutableClass) {
                if (this.myMutableClass == null) {
                    this.myMutableClass = myMutableClass;
                } else {
                    assertNotEquals(this.myMutableClass, myMutableClass);
                }
            }
        };
        imutableSource
                .subscribe(saveAndCheckNext)
                .subscribe(saveAndCheckNext);

        //Assert that value not changes for if subscriber of immutable changes it
        imutableSource
                .subscribe(a -> a.anInt = 10)
                .subscribe(a -> assertNotEquals(10, a.anInt))
                .subscribe(a -> assertEquals(1, a.anInt));


        streamPipe.accept(mm);

    }

    @Test(expected = SameInstanceException.class)
    public void testImmuteException() {
        StreamPipe<Object> source = StreamPipe.create();
        source.immute(a -> a).subscribe(System.out::println);
        source.accept(1);
    }

    @Test
    public void testImmuteExceptionHandling() {
        CollectionSource.fromSingle(1)
                .addErrorHandling(IllegalStateException.class, a -> assertEquals(SameInstanceException.class, a.getClass())
                )
                .allowHandlingSuper(true)
                .immute(a -> a)
                .subscribe(o -> fail());
    }

    @Test
    public void testOfOptional() {
        CollectionSource.fromSingle(new MyMutableClass(0))
                .ofOptional()
                .subscribe(o -> assertTrue(o.isPresent()))
                .map(Optional::get)
                .subscribe(o -> assertEquals(0, o.anInt))
                .map(o -> null)
                .ofOptional()
                .subscribe(o -> assertFalse(o.isPresent()));
    }


    @Test(expected = NoErrorHandlerException.class)
    public void testErrorHandlingWrapper() {
        CollectionSource.fromSingle("A")

                //Checking sink exception in another pipe
                .addErrorHandling(
                        IllegalStateException.class,
                        StreamPipe.<IllegalStateException>create()
                                .subscribe(e -> assertEquals(IllegalStateException.class, e.getClass()))
                )

                //Handle exception directly
                .addErrorHandling(IllegalArgumentException.class, e -> assertEquals(IllegalArgumentException.class, e.getClass()))

                .subscribe(o -> {
                    throw new IllegalStateException();
                })
                .subscribe(o -> {
                    throw new IllegalArgumentException();
                })

                //Should be thrown cause no handler added
                .subscribe(o -> {
                    throw new IllegalAccessError();
                });


    }

    private static class MyMutableClass {
        int anInt;

        MyMutableClass(int anInt) {
            this.anInt = anInt;
        }
    }
}