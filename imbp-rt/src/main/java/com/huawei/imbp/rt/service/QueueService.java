package com.huawei.imbp.rt.service;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Charles(Li) Cai
 * @date 5/17/2019
 */
public class QueueService<T> {

    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();

    public void add(T t){
        queue.add(t);
    }

    public T poll(){
        return queue.poll();
    }

    public int size(){
        return queue.size();
    }

}
