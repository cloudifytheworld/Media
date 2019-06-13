package com.huawei.imbp.rt.service;

import akka.actor.ActorSystem;
import com.datastax.driver.core.*;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.rt.build.BuildStatement;
import com.huawei.imbp.rt.build.StatementBuildFactory;
import com.huawei.imbp.rt.common.Constant;
import com.huawei.imbp.rt.common.InputParameter;
import com.huawei.imbp.rt.common.JobStatus;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.Aoi;
import com.huawei.imbp.rt.thread.ThreadServiceManage;
import com.huawei.imbp.rt.entity.ClientData;
import com.huawei.imbp.rt.transfer.DataClient;
import com.huawei.imbp.rt.transfer.JobStorage;
import com.huawei.imbp.rt.util.EntityMappingUtil;
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
public class CassandraAsyncService extends DataAccessService {


    @Autowired
    JobStorage storage;

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    @Autowired
    public Session cassandraSession;

    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public StatementBuildFactory buildFactory;

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    @Value("${data.renderLimit}")
    public int renderLimit;

    @Value("${data.threadSize}")
    public int threadSize;

    @Value("${data.filePath}")
    public String filePath;

    @Value("${data.inMemoryWrite}")
    public boolean inMemoryWrite;

    private PreparedStatement statement;

    @PostConstruct
    //Todo refactor and init based on system input
    private void init(){
        statement = cassandraSession.prepare("SELECT * FROM images.aoi_single_component_image_1 where created_day = ? and device_type = ? and hour = ? and mins = ? and sec = ? ALLOW FILTERING");
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
        String system = input.getSystem();
        int port = input.getServerPort();
        boolean consolidation = input.isConsolidation();
        boolean dateTimeRange = input.isDateTimeRange();

        log.info("client info:-- "+input.toString());
        storage.put(groupId, clientId, input);

        CountDownLatch latch = new CountDownLatch(threadSize);
        QueueService<String> queue = new QueueService<>();
        ThreadServiceManage manage = new ThreadServiceManage(threadSize, total, queue, latch);
        boolean done = consolidation?manage.executeNet(server, port):
                manage.executeFile(filePath, groupId, inMemoryWrite);

        try {
            BuildStatement stmt = buildFactory.get(system, dateTimeRange);
            PreparedStatement prepStmt = stmt.build(system);
            Set<String> indexes = stmt.getIndex(system, date, startTime, endTime);
            List<ResultSetFuture> futuresData = new ArrayList<>();

            int indexSize = indexes.size();
            log.info("size of file in index "+indexSize);
            log.info(String.format("dateTime: %s - threadSize: %d - renderLimit: %d - consolidation: %s - inMemoryWrite: %s" +
                            " - filePath: %s", dateTimeRange+"", threadSize, renderLimit, consolidation+"", inMemoryWrite+"", filePath));

            indexes.stream().forEach( index -> {
                String[] keys = index.split("#");
                BoundStatement boundStatement = stmt.bind(keys, prepStmt);
                execute(count, boundStatement, indexSize, futuresData, queue);
            });

            //Todo mutex lock for exclusive send end of file msg
            IntStream.range(0, threadSize).forEach(i -> queue.add(Constant.END_MARKER));

            latch.await();
            manage.close();
            onEnd(groupId, clientId, server, port, JobStatus.complete);


        }catch (Exception e){
            manage.close();
            onEnd(groupId, clientId, server, port, JobStatus.fail);
            log.error(e);
        }

        long last = (System.currentTimeMillis() - start)/1000;
        log.info(date+" takes "+last+" seconds,  total size(M) "+total.get()/1000000);
    }

    private void onEnd(String groupId, String clientId, String server, int port, JobStatus status){

        DataClient senderClose = new DataClient(new InetSocketAddress(server, port));
        senderClose.write(ByteBuffer.wrap((
                Constant.END_MARKER + ":" + groupId + ":" + clientId + ":" + status).getBytes()));
        senderClose.close();
    }

    @Override
    public void execute(AtomicInteger count, BoundStatement boundStatement, int indexSize,
                        List<ResultSetFuture> futuresData, QueueService<String> queue) {

        ResultSetFuture resultSetFuture = cassandraSession.executeAsync(boundStatement);
        futuresData.add(resultSetFuture);

        if(count.incrementAndGet()%renderLimit == 0 || count.get() == indexSize){
            List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
            for (ListenableFuture<ResultSet> future : futureLists) {
                try {
                    ResultSet rs = future.get();
                    List<Row> rows = rs.all();
                    rows.stream().forEach(row -> {
                        //Todo base on system
                        Aoi aoi = EntityMappingUtil.mappingAoi(row);
                        queue.add(aoi.toString());
                    });

                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    }

    public void feedDataByDate(ClientData clientData){

        long start = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger();
        AtomicInteger countSize = new AtomicInteger();

        long startTime = clientData.getStartTime();
        long endTime = clientData.getEndTime();
        String date = clientData.getDate();
        boolean dateTimeRange = clientData.isDateTimeRange();
        String system = clientData.getSystem();
        QueueService<String> queue = clientData.getQueue();
        CountDownLatch latch = clientData.getValueLatch();

        try {
            BuildStatement stmt = buildFactory.get(system, dateTimeRange);
            PreparedStatement prepStmt = stmt.build(system);
            Set<String> indexes = stmt.getIndex(system, date, startTime, endTime);
            log.info(date + " index size: " + indexes.size());
            int indexSize = indexes.size();

            List<ResultSetFuture> futuresData = new ArrayList<>();

            indexes.stream().forEach(index -> {

                String[] keys = index.split("#");
                ResultSetFuture results = cassandraSession.executeAsync(stmt.bind(keys, prepStmt));
                futuresData.add(results);

                if (count.incrementAndGet() % renderLimit == 0 || count.get() == indexSize) {
                    List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
                    for (ListenableFuture<ResultSet> future : futureLists) {
                        try {
                            ResultSet rs = future.get();
                            List<Row> rows = rs.all();
                            countSize.addAndGet(rows.size());
                            rows.stream().forEach(d -> {
                                queue.add(date + "@" + d.getString("file_name"));
                            });
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                    latch.countDown();
                }
            });
            if (indexes.size() == 0) {
                latch.countDown();
            }
        }catch (Exception e){
            log.error(e);
        }
        Long end = (System.currentTimeMillis()-start)/1000;
        log.info(date+" takes seconds "+end+", and process cells "+countSize.get());
    }

}
