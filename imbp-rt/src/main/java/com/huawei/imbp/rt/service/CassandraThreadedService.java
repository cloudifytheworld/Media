package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.datastax.driver.core.*;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.rt.common.InputParameter;
import com.huawei.imbp.rt.common.JobStatus;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.Aoi;
import com.huawei.imbp.rt.entity.DateDevice;
import com.huawei.imbp.rt.transfer.ClientData;
import com.huawei.imbp.rt.transfer.DataSender;
import com.huawei.imbp.rt.transfer.JobStorage;
import com.huawei.imbp.rt.util.EntityMappingUtil;
import com.huawei.imbp.rt.util.OffHeapMemoryAllocation;
import com.huawei.imbp.rt.util.StatisticManager;
import com.huawei.imbp.rt.util.WriteToFile;
import lombok.extern.log4j.Log4j2;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
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
    JobStorage storage;

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

    @Value("${data.renderLimit}")
    public int renderLimit;


    private PreparedStatement statement;

    @PostConstruct
    private void init(){
//        statement = cassandraSession.prepare("SELECT * FROM images.aoi_single_component_image_1 where created_day = ? and device_type = ? and hour = ? and mins = ? ALLOW FILTERING");
        statement = cassandraSession.prepare("SELECT * FROM images.aoi_single_component_image_1 where created_day = ? and device_type = ? and hour = ? and mins = ? and sec = ? and label = ? ALLOW FILTERING");

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

                if(count.incrementAndGet()%renderLimit == 0 || count.get() == indexSize){
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

    public void getDataByDate(ClientData input){

        storage.put(input.getGroupId(), input.getClientId(), input);
        DateTimeFormatter dft = DateTimeFormat.forPattern("yyyyMMdd");
        String server = input.getServerIp();
        int port = input.getServerPort();
        String date = input.getStartDate();
        log.info(date);

        InetSocketAddress serverAddress = new InetSocketAddress(server, port);
        DataSender send = new DataSender(serverAddress);

        List<ResultSetFuture> futuresData = new ArrayList<>();
        long start = System.currentTimeMillis();

//        Set<String> indexes = redisTemplate.boundSetOps("date:"+input.getSystem() + ":" + date).members();
        Set<String> indexes = redisTemplate.boundZSetOps("secDate:"+input.getSystem() + ":" + date).rangeByScore(1541055600000l,1541141999000l);

        int indexSize = indexes.size();
        AtomicInteger count = new AtomicInteger();

        indexes.stream().forEach( index -> {
            String[] keys = index.split("#");
            ResultSetFuture resultSetFuture = cassandraSession.executeAsync(statement.bind(date, keys[0]
                    , Integer.parseInt(keys[1]), Integer.parseInt(keys[2]), Integer.parseInt(keys[3]), keys[4]));
            futuresData.add(resultSetFuture);

            if(count.incrementAndGet()%renderLimit == 0 || count.get() == indexSize){
                List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
                for (ListenableFuture<ResultSet> future : futureLists) {
                    try {
                        ResultSet rs = future.get();
                        List<Row> rows = rs.all();

                        rows.stream().forEach(s -> {
                            Aoi aoi = EntityMappingUtil.mappingAoi(s);
                            byte[] data = aoi.toString().getBytes();
                            StatisticManager.total += data.length;
                            ByteBuffer buffer = ByteBuffer.wrap(data);
                            send.write(buffer);
                            //WriteToFile.writeToFile(aoi);
                        });

                    } catch (Exception e) {
                        log.error(Throwables.getStackTraceAsString(e));
                    }
                }
            }
        });

        send.close(input.getGroupId()+":"+input.getClientId()+":"+ JobStatus.complete);
        long last = (System.currentTimeMillis() - start)/1000;
        log.info(date+" takes "+last+" seconds,  total size(M) "+String.format("%.2f", (StatisticManager.total/1000000)));
    }


    //Todo: anytime the queue is empty, stream will treat the it as done, try spring websocket as message
    public void feedDataByDates(String system, String date, QueueService<String> queueService, CountDownLatch valueLatch){

        List<ResultSetFuture> futuresData = new ArrayList<>();
        long start = System.currentTimeMillis();
        Set<String> indexes = redisTemplate.boundSetOps("date"+":"+system + ":" + date).members();
        log.info(date+" index size: "+indexes.size());
        int indexSize = indexes.size();
        AtomicInteger count = new AtomicInteger();
        AtomicInteger countSize = new AtomicInteger();
        AtomicDouble image = new AtomicDouble();

        indexes.stream().forEach( index -> {

            String[] keys = index.split("#");
            ResultSetFuture results = cassandraSession.executeAsync(statement.bind(date, keys[0]
                    , Integer.parseInt(keys[1]), Integer.parseInt(keys[2])));
            futuresData.add(results);

            if(count.incrementAndGet()%renderLimit == 0 || count.get() == indexSize){
                List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
                for (ListenableFuture<ResultSet> future : futureLists) {
                    try {
                        ResultSet rs = future.get();
                        List<Row> rows = rs.all();
                        countSize.addAndGet(rows.size());
                        rows.stream().forEach(d -> {
                            long imageSize = d.getBytes("image").array().length;
                            image.addAndGet((double)imageSize/1000000);
                            queueService.add(date+"@"+d.getString("file_name"));
                        });
                    } catch (Exception e) {
                        log.error(Throwables.getStackTraceAsString(e));
                    }
                    if(queueService.size() > 5000 ) valueLatch.countDown();
                }
            }
        });
        if(indexes.size() == 0) {
            valueLatch.countDown();
        }

        Long end = (System.currentTimeMillis()-start)/1000;
        log.info(date+" takes seconds "+end+", and process cells "+countSize.get()+", data in size(M) "+String.format("%.2f", image.get()));
    }

    public void feedDataByHour(String system, String date, int hour, QueueService<String> queueService, CountDownLatch valueLatch){

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

            if(count.incrementAndGet()%240 == 0 || count.get() == indexSize){
                List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
                for (ListenableFuture<ResultSet> future : futureLists) {
                    try {
                        ResultSet rs = future.get();
                        List<Row> rows = rs.all();
                        countSize.addAndGet(rows.size());
                        rows.stream().forEach(d -> {
                            long imageSize = d.getBytes("image").array().length;
                            image.addAndGet((double)imageSize/1000000);
                            queueService.add(hour+"#"+d.getString("file_name"));
                        });
                    } catch (Exception e) {
                        log.error(Throwables.getStackTraceAsString(e));
                    }
                    if(queueService.size() > 500 ) valueLatch.countDown();
                }
            }
        });
        if(indexes.size() == 0) {
            valueLatch.countDown();
        }

        Long end = (System.currentTimeMillis()-start)/1000;
        log.info(date+":"+hour+" seconds "+end+", and process cells "+countSize.get()+", data in size(M) "+String.format("%.2f", image.get()));

    }


}
