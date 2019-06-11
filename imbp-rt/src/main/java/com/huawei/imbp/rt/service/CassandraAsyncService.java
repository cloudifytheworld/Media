package com.huawei.imbp.rt.service;

import akka.actor.ActorSystem;
import com.datastax.driver.core.*;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.rt.common.Constant;
import com.huawei.imbp.rt.common.InputParameter;
import com.huawei.imbp.rt.common.JobStatus;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.Aoi;
import com.huawei.imbp.rt.thread.FileTask;
import com.huawei.imbp.rt.thread.NetTask;
import com.huawei.imbp.rt.thread.ThreadServiceManage;
import com.huawei.imbp.rt.transfer.ClientData;
import com.huawei.imbp.rt.transfer.DataClient;
import com.huawei.imbp.rt.transfer.DataWriter;
import com.huawei.imbp.rt.transfer.JobStorage;
import com.huawei.imbp.rt.util.EntityMappingUtil;
import com.huawei.imbp.rt.util.OffHeapMemoryAllocation;
import com.huawei.imbp.rt.util.StatisticManager;
import com.huawei.imbp.rt.util.WriteToFile;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * @author Charles(Li) Cai
 * @date 3/25/2019
 */

@Component
@Log4j2
@RefreshScope
public class CassandraAsyncService {


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

    @Value("${data.threadSize}")
    public int threadSize;

    @Value("${data.writeToLocal}")
    public boolean writeToLocal;

    @Value("${data.keyToSec}")
    public boolean keyToSec;

    @Value("${data.filePath}")
    public String filePath;

    @Value("${data.inMemoryWrite}")
    public boolean inMemoryWrite;

    private PreparedStatement statementSec;
    private PreparedStatement statementPrimary;
    private PreparedStatement statement;

    @PostConstruct
    //Todo refactor and init based on system input
    private void init(){
        statementSec = cassandraSession.prepare("SELECT * FROM images.aoi_single_component_image_1 where created_day = ? and device_type = ? and hour = ? and mins = ? and sec = ? ALLOW FILTERING");
        statement = cassandraSession.prepare("SELECT * FROM images.aoi_single_component_image_1 where created_day = ? and device_type = ? and hour = ? and mins = ? ALLOW FILTERING");
        statementPrimary = cassandraSession.prepare("SELECT * FROM images.aoi_single_component_image_1 where created_day = ? and device_type = ? and hour = ? and mins = ? and sec = ? " +
                "and label = ? and created_time = ?");

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

    //Todo: not enough memory
    //      1. send by socket from NetFile needs verification by real servers
    //      2. when keyToSec is false
    // Works to write one at time, justify speed by renderLimit and threadSize
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
        InetSocketAddress serverAddress = new InetSocketAddress(server, port);


        Set<String> indexes = keyToSec?
            redisTemplate.boundSetOps("date:" + input.getSystem() + ":" + date).members():
                redisTemplate.boundZSetOps("secDate:"+input.getSystem() + ":" + date).rangeByScore(startTime, endTime);
        int indexSize = indexes.size();
        log.info("size of file in index "+indexSize);

        CountDownLatch latch = new CountDownLatch(threadSize);
        ThreadServiceManage manage = new ThreadServiceManage(threadSize);
        QueueService<String> queue = new QueueService<>();
        DataWriter dataWriter = writeToLocal?new DataWriter(filePath, groupId, inMemoryWrite):null;
        IntStream.range(0, threadSize).forEach(s -> {
            Runnable task = writeToLocal?
                    new FileTask(s, queue, dataWriter, latch, total):
                    new NetTask(s, queue, new DataClient(serverAddress), latch, total);
            manage.submit(task);
        });

//        IntStream.range(0, 4).forEach( i -> queue.add(i+""));

        List<ResultSetFuture> futuresData = new ArrayList<>();

        indexes.stream().forEach( index -> {
            String[] keys = index.split("#");
            BoundStatement boundStatement = keyToSec?statementSec.bind(keys[0], keys[1]
                    , Integer.parseInt(keys[2]), Integer.parseInt(keys[3]), Integer.parseInt(keys[4])):
                    statementPrimary.bind(keys[0], keys[1], Integer.parseInt(keys[2]), Integer.parseInt(keys[3]),
                            Integer.parseInt(keys[4]), keys[5], new Timestamp(Long.parseLong(keys[6])));

            ResultSetFuture resultSetFuture = cassandraSession.executeAsync(boundStatement);
            futuresData.add(resultSetFuture);

            if(count.incrementAndGet()%renderLimit == 0 || count.get() == indexSize){
                List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
                for (ListenableFuture<ResultSet> future : futureLists) {
                    try {
                        ResultSet rs = future.get();
                        List<Row> rows = rs.all();
                        rows.stream().forEach(row -> {
                            Aoi aoi = EntityMappingUtil.mappingAoi(row);
                            queue.add(aoi.toString());
                        });

                    } catch (Exception e) {
                        log.error(Throwables.getStackTraceAsString(e));
                    }
                }
            }

        });

        try {
            //Todo mutex lock for exclusive send end of file msg
            IntStream.range(0, threadSize).forEach(i -> queue.add(Constant.END_MARKER));

            latch.await();
            manage.close();

            DataClient senderClose = new DataClient(serverAddress);
            senderClose.write(ByteBuffer.wrap((
                    Constant.END_MARKER + ":" + groupId + ":" + clientId + ":" + JobStatus.complete).getBytes()));
            senderClose.close();

        }catch (Exception e){
            log.error(e.getMessage());
        }

        long last = (System.currentTimeMillis() - start)/1000;
        log.info(date+" takes "+last+" seconds,  total size(M) "+total.get()/1000000);
    }


    public void feedDataByDates(String system, String date, QueueService<String> queueService, CountDownLatch valueLatch){

        List<ResultSetFuture> futuresData = new ArrayList<>();
        long start = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger();
        AtomicInteger countSize = new AtomicInteger();
        AtomicDouble image = new AtomicDouble();

        Set<String> indexes = redisTemplate.boundSetOps("date"+":"+system + ":" + date).members();
        log.info(date+" index size: "+indexes.size());
        int indexSize = indexes.size();


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
                    if(queueService.size() > 500 ) valueLatch.countDown();
                }
            }
        });
        if(indexes.size() == 0 || queueService.size() > 0) {
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
