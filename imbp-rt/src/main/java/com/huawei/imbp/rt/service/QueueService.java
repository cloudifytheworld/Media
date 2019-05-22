package com.huawei.imbp.rt.service;

import com.google.common.base.Throwables;
import lombok.extern.log4j.Log4j2;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Charles(Li) Cai
 * @date 5/17/2019
 */

@Log4j2
public class QueueService<T> {

    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();
    private AtomicInteger counter = new AtomicInteger();
    private int sleepLimit = 2000;

    public void setSleepLimit(int sleepLimit) {
        this.sleepLimit = sleepLimit;
    }

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

    public boolean hasNext(){
        return queue.isEmpty();
    }


    public void waitForValue(){

        while (hasNext()){
            try{
                Thread.sleep(sleepLimit);
            }catch (Exception e){
                log.error(e.getMessage());
                break;
            }
        }
        if(queue.peek().equals("Done")){
            queue.poll();
        }
    }
    public Stream<String> asStream(){

        Spliterator spliterator = Spliterators.spliteratorUnknownSize(queue.iterator(), Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, true);
    }

}

