package com.huawei.imbp.rt.service;


import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.base.Throwables;

import com.huawei.imbp.rt.common.Constant;
import com.huawei.imbp.rt.thread.FileReactiveTask;
import com.huawei.imbp.rt.thread.ThreadServiceManage;
import com.huawei.imbp.rt.entity.ClientData;
import com.huawei.imbp.rt.transfer.DataWriter;
import com.huawei.imbp.rt.transfer.JobStorage;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * @author Charles(Li) Cai
 * @date 4/14/2019
 */

@Component
@Log4j2
@RefreshScope
//Todo redo the class, not work for now
public class CassandraReactiveService {

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    @Autowired
    public ReactiveCassandraOperations cassandraDataSession;

    @Autowired
    public JobStorage storage;

    @Value("${data.renderLimit}")
    public int renderLimit;

    @Value("${data.threadSize}")
    public int threadSize;

    @Value("${data.filePath}")
    public String filePath;

    @Value("${data.inMemoryWrite}")
    public boolean inMemoryWrite;



    public void getDataByDate(ClientData input){

        AtomicInteger count = new AtomicInteger();
        AtomicLong total = new AtomicLong();
        long start = System.currentTimeMillis();

        String groupId= input.getGroupId();
        String clientId = input.getClientId();
        String date = input.getDate();
        String server = input.getServerIp();
        long startTime = input.getStartTime();
        long endTime = input.getEndTime();
        int port = input.getServerPort();

        log.info("client info "+input.toString());

        storage.put(groupId, clientId, input);

        CountDownLatch latch = new CountDownLatch(threadSize);

        Semaphore semaphore = new Semaphore(renderLimit);
        QueueService<String> queue = new QueueService<>();
        ThreadServiceManage manage = new ThreadServiceManage(threadSize, total, queue, latch);
        DataWriter dataWriter = new DataWriter(filePath, groupId, inMemoryWrite);

        IntStream.range(0, threadSize).forEach(s -> {
            manage.submit(new FileReactiveTask(s, queue, dataWriter, latch, total, semaphore));
        });
        Set<String> indexes = redisTemplate.boundZSetOps("secDate:"+input.getSystem() + ":" + date)
                .rangeByScore(startTime,endTime);
//        Set<String> indexes = redisTemplate.boundSetOps("date:"+input.getSystem() + ":" + date).members();

        indexes.stream().forEach( index -> {
            String[] keys = index.split("#");
            try {
                Select select = QueryBuilder.select().all().from("images", "aoi_single_component_image_1");

                select.where(QueryBuilder.eq("created_day", keys[0]))
                        .and(QueryBuilder.eq("device_type", keys[1]))
                        .and(QueryBuilder.eq("hour", Integer.parseInt(keys[2])))
                        .and(QueryBuilder.eq("mins", Integer.parseInt(keys[3])))
                        .and(QueryBuilder.eq("sec", Integer.parseInt(keys[4])))
                        .and(QueryBuilder.eq("label", keys[5]))
                        .and(QueryBuilder.eq("created_time", Long.parseLong(keys[6])));
//                        .allowFiltering();
                semaphore.acquire();
//                Flux<Aoi> aois = cassandraDataSession.select(select, Aoi.class);
//                aois.onBackpressureBuffer(1000).limitRate(2).subscribe( aoi -> {
//                    try {
//                        //semaphore.acquire();
//
////                    byte[] data = aoi.toString().getBytes();
////                    total.addAndGet(data.length);
////                    ByteBuffer buffer = ByteBuffer.wrap(data);
////                    //send.write(buffer);
////                    WriteToFile.writeToFile(aoi);
//                        queue.add(aoi.toString());
//                    }catch (Exception e){
//                        log.error(e.getMessage());
//                        semaphore.release();
//                    }
//
//                });
//                semaphore.release();
//                cassandraDataSession.selectOne(select, Aoi.class).subscribe(aoi ->{
//                    byte[] data = aoi.toString().getBytes();
//                    total.addAndGet(data.length);
//                    ByteBuffer buffer = ByteBuffer.wrap(data);
//                    //send.write(buffer);
//                    WriteToFile.writeToFile(aoi);
//                    semaphore.release();
//                    queue.add(aoi.toString());
//
//                });
                count.incrementAndGet();
            }catch (Exception e){
                log.error(Throwables.getStackTraceAsString(e));
                semaphore.release();
            }

        });

        try {
            IntStream.range(0, threadSize).forEach(i -> queue.add(Constant.END_MARKER));
            latch.await();
            manage.close();
        }catch (Exception e){
            log.error(e.getMessage());
        }

        long last = (System.currentTimeMillis() - start)/1000;
        log.info(date+" takes "+last+" seconds, total files "+count.get()+", total size(M) "+total.get()/1000000);

    }
}
