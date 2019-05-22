package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.datastax.driver.core.*;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.rt.common.InputParameter;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.DateDevice;
import com.huawei.imbp.rt.util.OffHeapMemoryAllocation;
import com.huawei.imbp.rt.util.StatisticManager;
import com.huawei.imbp.rt.util.WriteToFile;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Charles(Li) Cai
 * @date 3/25/2019
 */

@Component
@Log4j2
@RefreshScope
public class CassandraThreadedService {

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    @Autowired
    public Session cassandraSession;

    @Autowired
    public OffHeapMemoryAllocation offHeapMemoryAllocation;

    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    private PreparedStatement statement;

    @PostConstruct
    private void init(){
        statement = cassandraSession.prepare("SELECT * FROM images.aoi_single_component_image where created_day = ? and device_type = ? and hour = ? and mins = ? ALLOW FILTERING");
    }

    public void getDataByDates(InputParameter input){


        String[] dates = input.getFrom();
        List<ResultSetFuture> futuresData = new ArrayList<>();
        long start = System.currentTimeMillis();

        for(int i=0; i<dates.length; i++) {
            String date = dates[i].trim();
            log.info(date);
            Set<String> indexes = redisTemplate.boundSetOps(input.getSystem() + ":" + date).members();
            int indexSize = indexes.size();
            AtomicInteger count = new AtomicInteger();

            indexes.stream().forEach( index -> {
                String[] keys = index.split("#");
                ResultSetFuture resultSetFuture = cassandraSession.executeAsync(statement.bind(date, keys[0]
                        , Integer.parseInt(keys[1]), Integer.parseInt(keys[2])));
                futuresData.add(resultSetFuture);

                if(count.incrementAndGet()%120 == 0 || count.get() == indexSize){
                    List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
                    for (ListenableFuture<ResultSet> future : futureLists) {
                        try {
                            ResultSet rs = future.get();
                            List<Row> rows = rs.all();
                            int size = rows.size();
                            if (size > 0) {
                                String key = "createdDay-"+date+":deviceType-"+keys[0]+":hour-"+keys[1]+":minute-"+keys[2];
                                log.info(key + " size: " + size);
                                StatisticManager.put(key, size);
                                WriteToFile.writeToFile(rows, "created_day-" + date + ":device_type-" + keys[0], ":hour", Integer.parseInt(keys[1]));
                            }
                        } catch (Exception e) {
                            log.error(Throwables.getStackTraceAsString(e));
                        }
                    }
                }
            });
        }

        long last = (System.currentTimeMillis() - start)/1000;
        log.info(dates[0]+" takes "+last+" seconds, total "+StatisticManager.counter+" cells, total size(M) "+String.format("%.2f", StatisticManager.total));
    }

    public void feedDataByDates(String system, String date, QueueService<String> queueService){

        List<ResultSetFuture> futuresData = new ArrayList<>();
        long start = System.currentTimeMillis();
        Set<String> indexes = redisTemplate.boundSetOps("date"+":"+system + ":" + date).members();
        log.info(date+" index size: "+indexes.size());
        int indexSize = indexes.size();
        AtomicInteger count = new AtomicInteger();
        AtomicInteger countSize = new AtomicInteger();

        indexes.stream().forEach( index -> {

            String[] keys = index.split("#");
            ResultSetFuture results = cassandraSession.executeAsync(statement.bind(date, keys[0]
                    , Integer.parseInt(keys[1]), Integer.parseInt(keys[2])));
            futuresData.add(results);

            if(count.incrementAndGet()%60 == 0 || count.get() == indexSize){
                List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
                for (ListenableFuture<ResultSet> future : futureLists) {
                    try {
                        ResultSet rs = future.get();
                        List<Row> rows = rs.all();
                        countSize.addAndGet(rows.size());
                        rows.stream().forEach(d -> queueService.add(date+"@"+d.getString("file_name")));
                    } catch (Exception e) {
                        log.error(Throwables.getStackTraceAsString(e));
                    }
                }
            }
        });
        Long end = (System.currentTimeMillis()-start)/1000;
        log.info(date+" takes seconds "+end+", and process data "+countSize.get());
    }

    public void feedDataByHour(String system, String date, int hour, QueueService<String> queueService){

        List<ResultSetFuture> futuresData = new ArrayList<>();
        long start = System.currentTimeMillis();
        Set<String> indexes = redisTemplate.boundSetOps("hour"+":"+system + ":" + date+":"+hour).members();
        log.info(date+":"+hour+"-index size: "+indexes.size());
        int indexSize = indexes.size();
        AtomicInteger count = new AtomicInteger();
        AtomicInteger countSize = new AtomicInteger();
        AtomicDouble image = new AtomicDouble();

        indexes.stream().forEach( index -> {

            String[] keys = index.split("#");
            ResultSetFuture results = cassandraSession.executeAsync(statement.bind(date, keys[0]
                    ,hour, Integer.parseInt(keys[1])));
            futuresData.add(results);

            if(count.incrementAndGet()%120 == 0 || count.get() == indexSize){
                List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
                for (ListenableFuture<ResultSet> future : futureLists) {
                    try {
                        ResultSet rs = future.get();
                        List<Row> rows = rs.all();
                        WriteToFile.writeToFile(rows, null, null, 0);
                        countSize.addAndGet(rows.size());
                        rows.stream().forEach(d -> {
                            long imageSize = d.getBytes("image").array().length;
                            image.addAndGet((double)imageSize/1000000);
                            queueService.add(d.getString("file_name"));
                        });
                    } catch (Exception e) {
                        log.error(Throwables.getStackTraceAsString(e));
                    }
                }
            }
        });
        if(countSize.get() == 0) {
            queueService.add("Done");
        }
        Long end = (System.currentTimeMillis()-start)/1000;
        log.info(date+":"+hour+" seconds "+end+", and process cells "+countSize.get()+", data in size(M) "+image.get());
    }


}
