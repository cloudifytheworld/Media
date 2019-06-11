package com.huawei.imbp.rt.thread;

import com.huawei.imbp.rt.common.Constant;
import com.huawei.imbp.rt.service.QueueService;
import com.huawei.imbp.rt.transfer.DataWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Charles(Li) Cai
 * @date 6/6/2019
 */

@Log4j2
public class FileTask implements Runnable{

    @Value("${data.sleepLimit}")
    public int sleepLimit;

    @Value("${data.inMemoryWrite}")
    public boolean inMemoryWrite;

    QueueService<String> queue;
    CountDownLatch latch;
    DataWriter writer;
    AtomicLong bytes;
    int seq;


    public FileTask(int seq, QueueService queue, DataWriter writer, CountDownLatch latch, AtomicLong bytes){
        this.seq = seq;
        this.queue = queue;
        this.latch = latch;
        this.bytes = bytes;
        this.writer = writer;
    }
    @Override
    public void run() {

        try {

            Thread.sleep(sleepLimit);

            while (true) {
                String aoi = queue.poll();
                if(aoi !=null) {
                    if(aoi.equals(Constant.END_MARKER))break;
                    byte[] data = aoi.getBytes();
                    bytes.addAndGet(data.length);
                    if(inMemoryWrite) {
                        writer.writeToFile(ByteBuffer.wrap(data));
                    }else {
                        writer.writeToFile(data);
                    }
                }
            }
            if(inMemoryWrite) writer.close();
        }catch (Exception e){
            log.error(e);
        }
        latch.countDown();
        log.info(seq+" Finish "+Thread.currentThread().getName());
    }
}
