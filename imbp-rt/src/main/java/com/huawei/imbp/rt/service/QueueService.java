package com.huawei.imbp.rt.service;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Charles(Li) Cai
 * @date 5/17/2019
 */

public class QueueService<T> {

    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();
    private AtomicInteger counter = new AtomicInteger();

    public void add(T t){
        queue.add(t);
    }

    public T poll(){
        counter.incrementAndGet();
        return queue.poll();
    }

    public int size(){
        return queue.size();
    }

    public int getCount(){
        return counter.get();
    }



}
