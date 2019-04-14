package com.lonelydeveloper97.dpipeline;

import com.lonelydeveloper97.dpipeline.collections.CollectionSource;
import com.lonelydeveloper97.dpipeline.collections.Collector;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PipelineTest {

    //Very complex test. Here we create tree of pipelines.
    //
    //The Root - is simple string pipeline.
    //We attach first "pipeline" to it.
    //
    //1st Pipeline
    //This branch aggregates your string values by pairs, and then zip this pairs with another source of 3 elements.
    //(with default strategy, that finds a pair for each element by indexing them)
    //As a result we receive strings that were build by concatenation of 3 elements - 2 from root and one from another source.
    //7th element has not got a pair, so won't arrive to the end of this pipeline
    //
    //The Root transforms with filter of empty string and creates another source with 6 elements
    //To this new source we connect 2 pipelines
    //
    //2nd Pipeline
    //Collects first four elements to list. Because we will receive only 6 elements list can be collected only once
    //
    //3rd Pipeline
    //Firstly in collects elements to lists of 2
    //Then we connect to it 4th pipeline, , but before it we "immute" our source of lists.
    //It makes 4th pipeline and rest of 3rd pipeline receive their own copies of list elements.
    //The 4th pipeline modifies the list, by adding element to it, but it doesn't affect 3rd pipeline flow.
    //If you'll remove "immute" call, you can see that test will fail
    //After it both of pipelines making strings from lists, and check results - nothing interesting here
    //
    //The filtered source transforms using another one filter.
    //Now we have a source with strings, that have length < 1, and because we have removed all empty strings before
    //It should contain only 2 strings with length 1: "a", "1"
    //Our last pipeline connected here
    //
    //5th pipeline
    //Simply parses Integer and handles an exception that will be thrown then parsing "a" item.
    //If exception occurs it will be received by 6th pipeline, that transforms the exception to message and checks this message.
    //
    //Our last row passes values to our Root
    //
    //Interesting fact: this code without comments takes 38 rows and this explanation of what does it do - 37
    //So it seems like this tool is really interesting if you want to describe some data processing

    @Test
    public void pipelineTest() {
        AtomicInteger called = new AtomicInteger();

        Pipeline.<String>createVar()//The ROOT

                // 1st pipeline
                .subscribe(Pipeline.<String>createVar()
                        .collect(Collector.stringCollector(), 2)
                        .zip(CollectionSource.fromIterable(Arrays.asList("!", "?", ")")))
                        .map(pair -> pair.getFirst().get() + pair.getSecond().get())
                        .subscribe(s -> assertTrue(s.equals("AABV!") || s.equals("CCa?") || s.equals("1)")))
                        .subscribe(s -> called.incrementAndGet())
                )

                //root transformation
                .filterNot(String::isEmpty)

                //2nd pipeline
                .subscribe(Pipeline.<String>createVar()
                        .collect(Collector.listCollector(), 4)
                        .subscribe(l -> assertEquals("a", l.get(l.size() - 1)))
                        .subscribe(l -> called.incrementAndGet())
                )

                //3rd pipeline
                .subscribe(Pipeline.<String>createVar()
                        .collect(Collector.listCollector(), 2)
                        .immute(ArrayList::new)

                        //4th pipeline that is child of 3rd
                        .subscribe(Pipeline.<List<String>>createVar()
                                .map(l -> {
                                    l.add("1");
                                    return l;
                                })
                                .map(l -> CollectionSource.fromIterable(l).collectAll(Collector.stringCollector()))
                                .subscribe(s -> assertTrue(s.equals("AABV1") || s.equals("CCa1") || s.equals("1$$1")))
                                .subscribe(l -> called.incrementAndGet())
                        )

                        //3rd pipeline continue
                        .map(l -> CollectionSource.fromIterable(l).collectAll(Collector.stringCollector(s -> s)))
                        .zip(CollectionSource.fromIterable(Arrays.asList("!", "?", ")")))
                        .map(pair -> pair.getFirst().get() + pair.getSecond().get())
                        .subscribe(s -> assertTrue(s.equals("AABV!") || s.equals("CCa?") || s.equals("1$$)")))
                        .subscribe(s -> called.incrementAndGet())
                )

                //another root transformation
                .filter(s -> s.length() < 2)

                //5th pipeline
                .subscribe(Pipeline.<String>createVar()
                        //6th pipeline
                        .addErrorHandling(NumberFormatException.class, Pipeline.<NumberFormatException>createVar()
                                .map(Exception::getMessage)
                                .subscribe(m -> assertTrue(m.contains("a")))
                                .subscribe(m -> called.incrementAndGet())
                        )
                        .map(Integer::parseInt)
                        .subscribe(integer -> assertEquals(1, 1))
                        .subscribe(i -> called.incrementAndGet())
                )

                //Values that wille be passed to root
                .acceptAll(Arrays.asList("AA", "BV", "CC", "a", "1", "", "$$"));

        assertEquals(12, called.get());
    }

}