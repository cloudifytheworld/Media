package com.huawei.imbp.rt.thread;

import com.google.common.base.Throwables;
import com.huawei.imbp.rt.service.QueueService;
import com.huawei.imbp.rt.transfer.DataClient;
import com.huawei.imbp.rt.transfer.DataWriter;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * @author Charles(Li) Cai
 * @date 6/6/2019
 */

@Log4j2
public class ThreadServiceManage {

    private ExecutorService executorService;
    private CountDownLatch latch;
    private QueueService<String> queue;
    private AtomicLong total;
    private int size;


    public ThreadServiceManage(int size, AtomicLong total, QueueService<String> queue, CountDownLatch latch){
        this.total = total;
        this.queue = queue;
        this.size = size;
        this.latch = latch;
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
            log.error(e);
        }
    }

    public boolean executeNet(String server, int port){

        IntStream.range(0, size).forEach(s -> {
            DataClient client = new DataClient(new InetSocketAddress(server, port));
            Runnable task = new NetTask(queue, client, latch, total);
            submit(task);
        });

        return true;
    }

    public boolean executeFile(String filePath, String groupId, boolean inMemoryWrite){

        IntStream.range(0, size).forEach(s -> {
            DataWriter writer = new DataWriter(filePath, groupId, inMemoryWrite);
            Runnable task = new FileTask(queue, writer, latch, total);
            submit(task);
        });

        return true;
    }
}


