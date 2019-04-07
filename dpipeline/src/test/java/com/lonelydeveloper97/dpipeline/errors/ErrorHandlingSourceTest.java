package com.lonelydeveloper97.dpipeline.errors;

import com.lonelydeveloper97.dpipeline.collections.CollectionSource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ErrorHandlingSourceTest {

    @Test
    public void allowHandlingSuper() {
        CollectionSource.fromSingle("a")
                .addErrorHandling(IllegalStateException.class, e -> assertEquals(SubclassOfIllegalException.class, e.getClass()))
                .allowHandlingSuper(true)
                .subscribe(e -> {
                    throw new SubclassOfIllegalException();
                });
    }

    @Test(expected = NoErrorHandlerException.class)
    public void notAllowHandlingSuper() {
        CollectionSource.fromSingle("a")
                .addErrorHandling(IllegalStateException.class, e -> fail())
                .subscribe(e -> {
                    throw new SubclassOfIllegalException();
                });
    }

    @Test
    public void superAndEqualHandlers() {
        CollectionSource.fromSingle("a")
                .addErrorHandling(IllegalStateException.class, e -> fail())
                .addErrorHandling(SubclassOfIllegalException.class, e -> assertEquals(SubclassOfIllegalException.class, e.getClass()))
                .subscribe(e -> {
                    throw new SubclassOfIllegalException();
                });
    }

    private static class SubclassOfIllegalException extends IllegalStateException {
    }
}