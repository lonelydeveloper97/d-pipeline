package com.lonelydeveloper97.dpipeline;

import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;
import com.lonelydeveloper97.dpipeline.util.function.Optional;

import org.junit.Test;

import static org.junit.Assert.*;

public class SourceTest {

    @Test
    public void testImmute() {
        Pipe<MyMutableClass> pipe = Pipe.create();

        MyMutableClass mm = new MyMutableClass(10);

        //Assert instances are the same for mutable
        pipe.subscribe(a -> assertEquals(a, mm));

        //Assert that instance was mutated
        pipe.subscribe(a -> a.anInt = 1)
                .subscribe(a -> assertEquals(1, a.anInt));


        //Assert that instances are different but has the same value
        Source<MyMutableClass> imutableSource = pipe.immute(a -> new MyMutableClass(a.anInt))
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


        pipe.accept(mm);

    }

    @Test(expected = IllegalStateException.class)
    public void testImmuteException() {
        Pipe<Object> source = Pipe.create();
        source.immute(a -> a).subscribe(System.out::println);
        source.accept(1);
    }

    @Test
    public void testImmuteExceptionHandling() {
        CollectionSource.fromSingle(1)
                .addErrorHandling(IllegalStateException.class,
                        Pipe.<IllegalStateException>create().subscribe(a -> assertEquals(IllegalStateException.class, a.getClass()))
                )
                .immute(a -> a)
                .subscribe(o-> fail());
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


    @Test(expected = IllegalAccessError.class)
    public void testErrorHandlingWrapper() {
        CollectionSource.fromSingle("A")
                .addErrorHandling(
                        IllegalStateException.class,
                        Pipe.<IllegalStateException>create()
                                .subscribe(e -> assertEquals(IllegalStateException.class, e.getClass()))
                )
                .addErrorHandling(IllegalArgumentException.class,
                        Pipe.<IllegalArgumentException>create()
                                .subscribe(e -> assertEquals(IllegalArgumentException.class, e.getClass())))
                .subscribe(o -> {
                    throw new IllegalStateException();
                })
                .subscribe(o -> {
                    throw new IllegalArgumentException();
                })
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