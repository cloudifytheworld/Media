package com.fw.imbp.rt.thread;

import com.fw.imbp.rt.common.Constant;
import com.fw.imbp.rt.service.QueueService;
import com.fw.imbp.rt.transfer.DataWriter;
import lombok.extern.log4j.Log4j2;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Charles(Li) Cai
 * @date 6/6/2019
 */

@Log4j2
public class FileReactiveTask implements Runnable{

    Semaphore semaphore;
    QueueService<String> queue;
    CountDownLatch latch;
    DataWriter writer;
    AtomicLong bytes;
    int seq;


    public FileReactiveTask(int seq, QueueService queue, DataWriter writer, CountDownLatch latch, AtomicLong bytes, Semaphore semaphore){
        this.seq = seq;
        this.queue = queue;
        this.latch = latch;
        this.bytes = bytes;
        this.writer = writer;
        this.semaphore = semaphore;
    }
    @Override
    public void run() {

        try {

            while (true) {
                String aoi = queue.poll();
                if(aoi !=null) {
                    if(aoi.equals(Constant.END_MARKER))break;
                    byte[] data = aoi.getBytes();
                    bytes.addAndGet(data.length);
                    writer.writeToFile(ByteBuffer.wrap(data));
                    semaphore.release();
                }
            }
            writer.close();
        }catch (Exception e){
            log.error(e);
        }
        latch.countDown();

        log.info(seq+" Finish "+Thread.currentThread().getName());
    }
}
