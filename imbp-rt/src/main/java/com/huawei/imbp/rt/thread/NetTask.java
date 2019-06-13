package com.huawei.imbp.rt.thread;

import com.huawei.imbp.rt.common.Constant;
import com.huawei.imbp.rt.service.QueueService;
import com.huawei.imbp.rt.transfer.DataClient;
import lombok.extern.log4j.Log4j2;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Charles(Li) Cai
 * @date 6/6/2019
 */

@Log4j2
public class NetTask implements Runnable{


    QueueService<String> queue;
    DataClient send;
    CountDownLatch latch;
    AtomicLong bytes;

    public NetTask(QueueService queue, DataClient send, CountDownLatch latch, AtomicLong bytes){

        this.queue = queue;
        this.send = send;
        this.latch = latch;
        this.bytes = bytes;
    }

    @Override
    public void run() {

        try {

            while (true) {
                String aoi = queue.poll();
                if(aoi !=null) {
                    byte[] data = aoi.getBytes();
                    ByteBuffer buffer = ByteBuffer.wrap(data);
                    if(aoi.equals(Constant.END_MARKER)){
                        break;
                    }
                    send.write(buffer);
                    bytes.addAndGet(data.length);
                }
            }
        }catch (Exception e){
            log.error(e);
        }

        latch.countDown();
        log.info("Done "+Thread.currentThread().getName());
        send.close();
    }
}
