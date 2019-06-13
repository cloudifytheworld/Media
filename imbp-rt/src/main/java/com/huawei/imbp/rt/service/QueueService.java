package com.huawei.imbp.rt.service;


import lombok.extern.log4j.Log4j2;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Charles(Li) Cai
 * @date 5/17/2019
 */

@Log4j2
public class QueueService<T> {

    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();
    private int sleepLimit;

    public QueueService(int sleepLimit){
        this.sleepLimit = sleepLimit;
    }

    public QueueService(){}

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

        try {
            Thread.sleep(sleepLimit);
        }catch (Exception e){
            log.error(e);
        }

        Spliterator spliterator = Spliterators.spliteratorUnknownSize(queue.iterator(), Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, true);
    }

}

