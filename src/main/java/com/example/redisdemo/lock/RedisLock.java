package com.example.redisdemo.lock;

public interface RedisLock {

    String getName();

    boolean tryLock();

    void unlock();
}
