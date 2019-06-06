package com.huawei.imbp.rt.service;


import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public boolean hasNext(){
        return queue.isEmpty();
    }

    public Stream<String> asStream(){

        Spliterator spliterator = Spliterators.spliteratorUnknownSize(queue.iterator(), Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, true);
    }

}

