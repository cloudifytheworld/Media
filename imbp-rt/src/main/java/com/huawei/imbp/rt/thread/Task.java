package com.huawei.imbp.rt.thread;

import com.huawei.imbp.rt.entity.Aoi;
import com.huawei.imbp.rt.service.QueueService;
import com.huawei.imbp.rt.transfer.DataSender;
import com.huawei.imbp.rt.util.WriteToFile;
import lombok.extern.log4j.Log4j2;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Charles(Li) Cai
 * @date 6/6/2019
 */

@Log4j2
public class Task implements Runnable{

    QueueService<Aoi> queue;
    DataSender send;
    CountDownLatch latch;
    AtomicLong bytes;
    int seq;

    public Task(int seq, QueueService queue, DataSender send, CountDownLatch latch, AtomicLong bytes){
        this.seq = seq;
        this.queue = queue;
        this.send = send;
        this.latch = latch;
        this.bytes = bytes;
    }
    @Override
    public void run() {

        try {

            Thread.sleep(5000);
            while (!queue.hasNext()) {
                Aoi aoi = queue.poll();
                byte[] data = aoi.toString().getBytes();
                bytes.addAndGet(data.length);
                WriteToFile.writeToFile(data);
//                byte[] data = aoi.toString().getBytes();
//                ByteBuffer buffer = ByteBuffer.wrap(data);
//                send.write(buffer);
            }
        }catch (Exception e){
            log.error(e);
        }
        latch.countDown();
        log.info(seq+" Finish "+Thread.currentThread().getName());
    }
}
