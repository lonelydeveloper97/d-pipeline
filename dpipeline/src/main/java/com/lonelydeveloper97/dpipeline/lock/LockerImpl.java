package com.lonelydeveloper97.dpipeline.lock;

import com.lonelydeveloper97.dpipeline.Sink;
import com.lonelydeveloper97.dpipeline.Source;
import com.lonelydeveloper97.dpipeline.pipe.var.VarPipe;

public class LockerImpl implements Locker, Source<Boolean> {
    private boolean isLocked;
    private VarPipe<Boolean> varPipe = VarPipe.create();

    LockerImpl(boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Override
    public void lock() {
        if (!isLocked)
            varPipe.accept(isLocked = true);
    }

    @Override
    public void unlock() {
        if (isLocked)
            varPipe.accept(isLocked = false);
    }

    boolean isLocked() {
        return isLocked;
    }


    @Override
    public Source<Boolean> subscribe(Sink<Boolean> sink) {
        return varPipe.subscribe(sink);
    }

    @Override
    public void unsubscribe(Sink<Boolean> sink) {
        varPipe.unsubscribe(sink);
    }

    @Override
    public void clear() {
        varPipe.clear();
    }

    @Override
    public int size() {
        return varPipe.size();
    }
}
