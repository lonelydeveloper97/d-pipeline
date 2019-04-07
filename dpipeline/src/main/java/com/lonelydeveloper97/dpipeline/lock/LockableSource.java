package com.lonelydeveloper97.dpipeline.lock;

import com.lonelydeveloper97.dpipeline.SimpleSorce;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.pipe.Pipe;

public interface LockableSource<T> extends SimpleSorce<T> {

    default Source<T> addLocker(LockerImpl locker) {
        Pipe<T> lockedStatePipe = createPipe();
        Pipe<T> mainPipe = createPipe();

        subscribe(t -> {
            if (locker.isLocked()) {
                lockedStatePipe.accept(t);
            } else {
                mainPipe.accept(t);
            }
        });

        locker.subscribe(isLocked -> {
           if(!isLocked){
               lockedStatePipe.subscribe(mainPipe);
               lockedStatePipe.clear();
           }
        });

        return mainPipe;
    }
}
