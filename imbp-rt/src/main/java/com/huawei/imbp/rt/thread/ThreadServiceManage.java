package com.huawei.imbp.rt.thread;

import com.google.common.base.Throwables;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author Charles(Li) Cai
 * @date 6/6/2019
 */

@Log4j2
public class ThreadServiceManage {

    private ExecutorService executorService;

    public ThreadServiceManage(int size){
        this.executorService = Executors.newFixedThreadPool(size);
    }

    public void submit(Runnable task){
        executorService.submit(task);
    }

    public void close(){
        try {
            executorService.shutdown();
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
        }
    }

    public void execute(Runnable task, int threadSize){
        IntStream.range(0, threadSize).forEach(s -> submit(task));
    }
}


