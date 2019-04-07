package com.lonelydeveloper97.dpipeline.lock;

public interface Locker {
    void lock();

    void unlock();

    default void fireCurrent(){
        unlock();
        lock();
    }

    static LockerImpl createLocked(){
        return new LockerImpl(true);
    }

    static LockerImpl createUnlocked(){
        return new LockerImpl(false);
    }
}
