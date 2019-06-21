package com.huawei.imbp.rt.service;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.rt.build.BuildStatement;
import com.huawei.imbp.rt.build.StatementBuildFactory;
import com.huawei.imbp.rt.common.Constant;
import com.huawei.imbp.rt.common.JobStatus;
import com.huawei.imbp.rt.entity.Aoi;
import com.huawei.imbp.rt.entity.ClientDateTime;
import com.huawei.imbp.rt.entity.FeedData;
import com.huawei.imbp.rt.thread.ThreadServiceManage;
import com.huawei.imbp.rt.entity.ClientData;
import com.huawei.imbp.rt.transfer.DataClient;
import com.huawei.imbp.rt.transfer.JobStorage;
import com.huawei.imbp.rt.util.EntityMappingUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

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
    public StatementBuildFactory buildFactory;

    @Autowired
    public Session cassandraSession;

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
        String system = input.getSystem();
        int port = input.getServerPort();
        boolean consolidation = input.isConsolidation();
        boolean dateTimeRange = input.isDateTimeRange();

        log.info("client info:-- "+input.toString());
        storage.put(groupId, clientId, input);

        CountDownLatch latch = new CountDownLatch(threadSize);
        QueueService<String> queue = new QueueService<>();
        ThreadServiceManage manage = new ThreadServiceManage(threadSize, total, queue, latch);
        consolidation = consolidation?manage.executeNet(server, port):
                manage.executeFile(filePath, groupId, inMemoryWrite);

        try {
            BuildStatement stmt = buildFactory.get(system, dateTimeRange);
            Set<String> indexes = stmt.getIndex(system, date, startTime, endTime);
            List<ResultSetFuture> futuresData = new ArrayList<>();

            int indexSize = indexes.size();
            log.info("size of file in index "+indexSize);
            log.info(String.format("dateTime: %s - threadSize: %d - renderLimit: %d - consolidation: %s - inMemoryWrite: %s" +
                            " - filePath: %s", dateTimeRange+"", threadSize, renderLimit, consolidation+"", inMemoryWrite+"", filePath));

            indexes.stream().forEach( index -> {
                String[] keys = index.split("#");
                BoundStatement boundStatement = stmt.bind(keys);
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

    public void feedDataByDate(FeedData feedData){

        long start = System.currentTimeMillis();

        List<ClientDateTime> dateTimes = feedData.getDateTimes();
        boolean dateTimeRange = feedData.isDateTimeRange();
        String system = feedData.getSystem();
        QueueService<String> queue = feedData.getQueue();
        CountDownLatch latch = feedData.getValueLatch();

        try {
            BuildStatement stmt = buildFactory.get(system, dateTimeRange);
            dateTimes.stream().forEach(date -> {
                AtomicInteger count = new AtomicInteger();
                AtomicInteger countSize = new AtomicInteger();

                Set<String> indexes = stmt.getIndex(system, date.getDate(), date.getStartTime(), date.getEndTime());
                int indexSize = indexes.size();
                log.info(String.format("%s index size: %d - dateTimeRange: %s", date.getDate(), indexSize, dateTimeRange+""));

                List<ResultSetFuture> futuresData = new ArrayList<>();

                indexes.stream().forEach(index -> {

                    String[] keys = index.split("#");
                    ResultSetFuture results = cassandraSession.executeAsync(stmt.bind(keys));
                    futuresData.add(results);

                    if (count.incrementAndGet() % renderLimit == 0 || count.get() == indexSize) {
                        List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
                        for (ListenableFuture<ResultSet> future : futureLists) {
                            try {
                                ResultSet rs = future.get();
                                List<Row> rows = rs.all();
                                countSize.addAndGet(rows.size());
                                rows.stream().forEach(d -> {
                                    //Todo change to specific requirement for data in queue
                                    queue.add(date.getDate() + "@" + d.getString("file_name"));
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
                Long end = (System.currentTimeMillis()-start)/1000;
                log.info(date+" takes seconds "+end+", and process cells "+countSize.get());
            });
        }catch (Exception e){
            log.error(e);
        }
    }

}
