package com.huawei.imbp.imbprt.service;

import akka.actor.ActorRef;
import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.BuiltStatement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.huawei.imbp.imbprt.Entity.Aoi;
import com.huawei.imbp.imbprt.util.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * @author Charles(Li) Cai
 * @date 4/8/2019
 */

@Component
@Log4j2
public class CassandraService extends DataRepository{

    //Test: take date as input, 20181103 (368123) and 20181105 (333433) has about 10G data,
    //Result: first request takes 260 seconds, the second request take 110 seconds
//    public void getData(String created_day, String device_type, String from, String to){
//
//        log.debug("build retrieve query");
//
//        PreparedStatement statement = session.prepare("SELECT * FROM images.aoi_single_component_image where created_day = ? ALLOW FILTERING");
//        try {
//            String[] dates = {from, to};
//            List<List<Row>> resultsData = new ArrayList<>();
//            long start = System.currentTimeMillis();
//
//            List<ResultSetFuture> futures = new ArrayList<>();
//            Arrays.stream(dates).forEach( date -> {
//                ResultSetFuture resultSetFuture = session.executeAsync(statement.bind(date));
//                futures.add(resultSetFuture);
//            });
//            List<ListenableFuture<ResultSet>> futureList = Futures.inCompletionOrder(futures);
//
//            for(ListenableFuture<ResultSet> future: futureList){
//                ResultSet rs = future.get();
//                List<Row> row = rs.all();
//                int size = row.size();
//                log.info("size: "+size);
//            }
//
////            for (ResultSetFuture future : futures) {
////                ResultSet rows = future.getUninterruptibly();
////                List<Row> row = rows.all();
////                int size = row.size();
////                log.info("size: "+size);
////                if(size != 0)
////                    resultsData.add(row);
////            }
////            String[] dates = {from, to};
////            List<List<Row>> resultsData = new ArrayList<>();
////            long start = System.currentTimeMillis();
////            Arrays.stream(dates).forEach( date -> {
////                ResultSetFuture resultSetFuture = session.executeAsync(statement.bind(date));
////                            Futures.addCallback(resultSetFuture, new FutureCallback<ResultSet>() {
////                                @Override
////                                public void onSuccess(ResultSet resultSet) {
////                                    List<Row> rows = resultSet.all();
////                                    log.info(rows.size());
////                                    resultsData.add(rows);
////                                }
////                                @Override
////                                public void onFailure(Throwable throwable) {
////                                    log.error(throwable.getMessage());
////                                }
////                            });
////            });
//            long end = System.currentTimeMillis() - start;
//            log.info(end/1000+" seconds");
//            log.info("done");
//        }catch (Exception e){
//            log.error(e.getMessage());
//        }
//    }

    //Test: load all input, with data 20181103 (368123)
    //Result: 615 seconds
    public void getData(String created_day, String device_type, String from, String to){
//        PreparedStatement statement = session.prepare("SELECT * FROM images.aoi_single_component_image where created_day = ? and device_type = ? and hour >= ? and hour < ? ALLOW FILTERING");
//        PreparedStatement deviceTypeSt = session.prepare("SELECT distinct created_day, device_type, hour FROM images.aoi_single_component_image where created_day = ? ALLOW FILTERING");
        PreparedStatement deviceTypeSt = session.prepare("SELECT * FROM images.aoi_single_component_image where created_day = ? ALLOW FILTERING");
        PreparedStatement statement = session.prepare("SELECT * FROM images.aoi_single_component_image where created_day = ? and device_type = ? and hour = ? ALLOW FILTERING");
        //PreparedStatement statement = session.prepare("SELECT * FROM test_images.aoi_single_element_image_1 where created_day = ? and device_type = ? and hour >= ? and hour < ? ALLOW FILTERING");
//        PreparedStatement deviceTypeSt = session.prepare("SELECT * FROM test_images.aoi_single_element_image_1 where created_day = ? ALLOW FILTERING");

        offHeapMemoryAllocation.getAddress(61474L );

        try{
            String[] dates = {from};
            long start = System.currentTimeMillis();
            List<ResultSetFuture> futures = new ArrayList<>();
            Arrays.stream(dates).forEach( date -> {
                ResultSetFuture resultSetFuture = session.executeAsync(deviceTypeSt.bind(date));
                futures.add(resultSetFuture);
            });

            List<ListenableFuture<ResultSet>> futureList = Futures.inCompletionOrder(futures);

            List<Integer> index = new ArrayList<>();

            int howMany = 3;
            ByteBuffer[] byteBuffers = new ByteBuffer[howMany];
            for(int i=0; i<howMany; i++) {
                byteBuffers[i] = ByteBuffer.allocateDirect(Integer.MAX_VALUE);
            }
            for(ListenableFuture<ResultSet> future: futureList){
                ResultSet rs = future.get();
                List<Row> rows = rs.all();
                int size = rows.size();
                log.info("size: "+size);
                if(size != 0) {
                    for(int y=0; y< rows.size(); y++) {
                        Aoi aoi = EntityMappingUtil.mappingAoi(rows.get(y));
                        byte[] bytes = ObjectConversion.toByteArray(aoi);
                        int length = bytes.length;
                        int which = y%howMany;
                        if(length > byteBuffers[which].remaining()) break;
                        byteBuffers[which].put(bytes, 0, length);
                        index.add(length);
                    }
                }
            }

            WriteToFile.writeToFile(byteBuffers, howMany, index);
            long end = System.currentTimeMillis() - start;
            log.info(end/1000+" seconds");
            final Semaphore semaphore = new Semaphore(13);

            int counters = 0;
            final SortedMap<String, Integer> resultsData = new TreeMap<>();


//            List<ResultSetFuture> futuresData = new ArrayList<>();
//                for(int i=0; i<results.size(); i++) {
//                    List<Row> rows = results.get(i);
//                    for(int y=0; y<rows.size(); y++) {
//
//                       Row row = rows.get(y);
//                       String device = row.getString("device_type");
//                       String date = row.getString("created_day");
//                       int hour = row.getInt("hour");
//                       try {
//                           semaphore.acquire();
//                                ResultSetFuture resultSetFuture = session.executeAsync(statement.bind(date, device, hour));
//                                futuresData.add(resultSetFuture);
//
//                            } catch (Exception e) {
//                                semaphore.release();
//                                log.error(e.getMessage());
//                            }
//                        if((y+1)%13 == 0) {
//                            List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);
//
//                            for (ListenableFuture<ResultSet> future : futureLists) {
//                                try {
//                                    ResultSet rs = future.get();
//                                    List<Row> r = rs.all();
//                                    int size = r.size();
//                                    if (size > 0) {
//                                        String deviceType = row.getString("device_type");
//                                        String label = r.get(0).getString("label");
//                                        String key = date + "_" + deviceType + "_" + label;
//                                        log.info(date + " " + deviceType + " " + label + " " + r.get(0).getString("file_name") + " " + size);
//                                        Integer value = resultsData.get(key);
//                                        if (value == null) {
//                                            resultsData.put(key, size);
//                                        } else {
//                                            resultsData.put(key, value + size);
//                                        }
//                                        counter =+size;
//                                    }
//                                    semaphore.release();
//                                } catch (Exception e) {
//                                    semaphore.release();
//                                    log.error(e.getMessage());
//                                }
//                            }
//                        }
//                    }
//
//                }

//            List<ListenableFuture<ResultSet>>   futureList  = Futures.inCompletionOrder(futuresData);
//
//            for(ListenableFuture<ResultSet> future: futureList){
//                try {
//                    ResultSet rs = future.get();
//                    List<Row> row = rs.all();
//                    int size = row.size();
//                    log.info("size: " + size);
//                }catch (Exception e){
//                    log.error(e.getMessage());
//                }
//            }
            long finalEnd = System.currentTimeMillis() - start;
            log.info(finalEnd/1000+" seconds");
            log.info("total "+counters);
        }catch (Exception ex){
            log.error(ex.getMessage());
        }
    }

    //Test: load all input, with data 20181103 (368123)
    //Result: 469 seconds, 360 seconds
    //With akka: 120 seconds to finish render data from cassandra, then takes 4 mins to write data to file
    //Setup: three cassandra cluster servers, each with max 5g memory, G1GC java, partition key are created_day, device_type, hour and minus
    //Result: it takes 82 seconds to save data to file for total 368123
    public void getData(String from, String to){

        String[] dates = {from};
        Set<String> dateDevice = new HashSet<>();
        final Semaphore semaphore = new Semaphore(6);

        PreparedStatement deviceTypeSt = session.prepare("SELECT distinct created_day, device_type, hour, mins FROM images.aoi_single_component_image where created_day = ? ALLOW FILTERING");
//        PreparedStatement deviceTypeSt = session.prepare("SELECT * FROM images.aoi_single_component_image where created_day = ? ALLOW FILTERING");
        PreparedStatement statement = session.prepare("SELECT * FROM images.aoi_single_component_image where created_day = ? and device_type = ? and hour = ? and mins = ? ALLOW FILTERING");

        long start = System.currentTimeMillis();
        try {
            List<ResultSetFuture> futures = new ArrayList<>();
            Arrays.stream(dates).forEach(date -> {
                ResultSetFuture resultSetFuture = session.executeAsync(deviceTypeSt.bind(date));
                futures.add(resultSetFuture);
            });

            List<ListenableFuture<ResultSet>> futureList = Futures.inCompletionOrder(futures);

            for (ListenableFuture<ResultSet> future : futureList) {
                ResultSet rs = future.get();
                List<Row> rows = rs.all();
                int size = rows.size();
                log.info("size: " + size);
                if (size != 0) {
                    dateDevice = rows.stream().map( r ->
                            r.getString("created_day")+"_"+r.getString("device_type")
                    ).collect(Collectors.toSet());
//                    for(int i =0; i<rows.size(); i++)
//                    log.info(rows.get(i).getString("image"));
                }
            }
            log.info("It takes to render metadata seconds " +(System.currentTimeMillis() - start)/1000);
            List<ResultSetFuture> futuresData = new ArrayList<>();
            int counter = 0;

            Iterator<String> itr = dateDevice.iterator();
            while (itr.hasNext()){
                try {
                    String[] v = itr.next().split("_");
                    for (int i = 0; i < 13; i++) {
                        for (int x = 0; x < 6; x++) {
                            semaphore.acquire();
                            ResultSetFuture resultSetFuture = session.executeAsync(statement.bind(v[0], v[1], i, x));
                            futuresData.add(resultSetFuture);
                        }

                        List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);

                        for (ListenableFuture<ResultSet> future : futureLists) {
                            try {
                                ResultSet rs = future.get();
                                List<Row> rows = rs.all();
                                int size = rows.size();
                                if (size > 0) {
                                    String key = "created_day-"+v[0]+":device_type-"+v[1]+":hour-"+i;
                                    log.info(key+" size: "+size);
                                    StatisticManager.put(key, size);
                                    //WriteToFile.writeToFile(rows, key);
                                    WriteToFile.writeToFile(rows, "created_day-"+v[0]+":device_type-"+v[1], ":hour", i);
                                    //fileAction.tell(rows, ActorRef.noSender());
                                    counter += size;
                                }
                                semaphore.release();
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        }
                    }
                }catch(Exception e){
                    log.error(Throwables.getStackTraceAsString(e));
                    semaphore.release();
                }
            }

            long finalEnd = System.currentTimeMillis() - start;
            log.info("it takes "+finalEnd/1000+" seconds to save data to file for total "+counter+" size(M) "+String.format("%.2f", StatisticManager.total));
            Gson gson = new Gson();
            String map = gson.toJson(StatisticManager.statistics);
            String deviceStats = gson.toJson(StatisticManager.deviceSize);
            String eachHourStats = gson.toJson(StatisticManager.eachHourSize);
            String eachMinusStats = gson.toJson(StatisticManager.eachMinusSize);
            log.info(map);
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
        }
    }
}
