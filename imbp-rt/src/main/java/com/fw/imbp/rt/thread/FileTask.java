package com.fw.imbp.rt.thread;

import com.fw.imbp.rt.common.Constant;
import com.fw.imbp.rt.service.QueueService;
import com.fw.imbp.rt.transfer.DataWriter;
import lombok.extern.log4j.Log4j2;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Charles(Li) Cai
 * @date 6/6/2019
 */

@Log4j2
public class FileTask implements Runnable{


    boolean inMemoryWrite;

    QueueService<String> queue;
    CountDownLatch latch;
    DataWriter writer;
    AtomicLong bytes;


    public FileTask(QueueService queue, DataWriter writer, CountDownLatch latch, AtomicLong bytes){

        this.queue = queue;
        this.latch = latch;
        this.bytes = bytes;
        this.writer = writer;
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
        log.info("Finish "+Thread.currentThread().getName());
    }
}
