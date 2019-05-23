package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.rt.common.InputParameter;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.entity.DateDevice;
import com.huawei.imbp.rt.repository.AoiRepository;
import com.huawei.imbp.rt.util.DataUtil;
import com.huawei.imbp.rt.util.WriteToFile;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import scala.Int;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Charles(Li) Cai
 * @date 4/14/2019
 */

@Component
@Log4j2
public class CassandraAsyncService {

    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AoiRepository aoiRepository;

    /*
     *  /api/{system}/rt/single
     */
    public Mono<ServerResponse> getDataByOne(InputParameter input){

       Mono<AoiEntity> data = aoiRepository.findByKeyCreatedDayAndKeyDeviceType(input.getFrom()[0], input.getDeviceType());
       return ServerResponse.ok().body(BodyInserters.fromPublisher(data, AoiEntity.class));
    }

    public void getDataByDate(InputParameter input){

        String[] dates = input.getFrom();

        for(int i=0; i<dates.length; i++) {
            String date = dates[i].trim();
            log.info(date);
            Set<String> deviceTypes = redisTemplate.boundSetOps(input.getSystem() + ":" + date).members();
            for(int y=1; y<13; y++) {
                DateDevice dateDevice = new DateDevice();
                dateDevice.setDate(date);
                dateDevice.setDeviceTypes(deviceTypes);
                dateDevice.setHour(y);
                ActorRef readAction = actorSystem.actorOf(imbpRtActionExtension.props("readAction"));
                readAction.tell(dateDevice, ActorRef.noSender());
            }
        }
    }

    public void getDataByDates(InputParameter input){

        String[] dates = input.getFrom();

        for(int i=0; i<dates.length; i++) {
            String date = dates[i].trim();
            log.info(date);
            Set<String> indexes = redisTemplate.boundSetOps(input.getSystem() + ":" + date).members();
            DateDevice dateDevice = new DateDevice();
            dateDevice.setDate(date);
            dateDevice.setIndexes(indexes);
            ActorRef readByDatesAction = actorSystem.actorOf(imbpRtActionExtension.props("readByDatesAction"));
            readByDatesAction.tell(dateDevice, ActorRef.noSender());
        }
    }

    /*
     * /api/{system}/rt/page
     */
    public Mono<ServerResponse> getDataByPage(InputParameter input){
        Flux<AoiEntity> results = null;

        if(input.getCreatedTime() == null) {
            results = aoiRepository.findTop2ByKeyCreatedDayAndKeyDeviceType(input.getFrom()[0], input.getDeviceType());
        }else{
            results = aoiRepository.findTop2ByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinuteAndKeyLabelAndKeyCreatedTimeLessThan(
                    input.getFrom()[0], input.getDeviceType(), input.getHour(), input.getMinute(), input.getLabel(), input.getCreatedTime()
            );
        }

        return results.collectList().flatMap(l -> {
            Map<String, Object> result = new HashMap<>();
            if(l.size() == 0){
                result.put("status", "no data");
                return ServerResponse.ok().syncBody(result);
            }
            result.put("status", "success");
            result.put("input", l);
            result.put("previous", l.get(0).getKey());
            result.put("next", l.get(l.size()-1).getKey());
            return ServerResponse.ok().body(BodyInserters.fromObject(result));

        });

    }

    /*
     * /api/{system}/rt/feeding
     */
    public void getDataByFeeding(String system, String from, QueueService<String> queueService){

        Semaphore semaphore = new Semaphore(60);
        String[] dates = DataUtil.convertStringToArray(from);

        for(int i=0; i<dates.length; i++) {
            String date = dates[i].trim();
            log.info(date);
            Set<String> indexes = redisTemplate.boundSetOps(system + ":" + date).members();
            indexes.stream().forEach( index -> {
                String[] keys = index.split("#");
                try {
                        semaphore.acquire();
                        Flux<AoiEntity> aoiEntityFlux = aoiRepository.findByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinute(
                                date, keys[0], Integer.parseInt(keys[1]), Integer.parseInt(keys[2]));
                        aoiEntityFlux.collectList().subscribe(s -> {
                            s.stream().forEach(entity ->
                            queueService.add(date+"@"+entity.getFileName()));
                            semaphore.release();
                        });

                }catch (Exception e){
                    log.error(Throwables.getStackTraceAsString(e));
                    semaphore.release();
                }
            });
        }
    }

    public void getDataByHourFeed(String system, String date, int hour, QueueService<String> queueService){

        Semaphore semaphore = new Semaphore(30);
        long start = System.currentTimeMillis();
        AtomicInteger countSize = new AtomicInteger();
        AtomicDouble image = new AtomicDouble();

        Set<String> indexes = redisTemplate.boundSetOps("hour"+":"+system + ":" + date+":"+hour).members();
        log.info(date+":"+hour+"async-index size: "+indexes.size());

        indexes.stream().forEach( index -> {
            String[] keys = index.split("#");
            try {
                semaphore.acquire();
                Flux<AoiEntity> aoiEntityFlux = aoiRepository.findByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinute(
                        date, keys[0], hour, Integer.parseInt(keys[1]));
                aoiEntityFlux.collectList().subscribe(s -> {
                    countSize.addAndGet(s.size());
                    s.stream().forEach(entity -> {
                        long imageSize = entity.getImage().array().length;
                        image.addAndGet((double)imageSize/1000000);
                        queueService.add(date+"@"+entity.getFileName());
                    });
                    semaphore.release();
                });
            }catch (Exception e){
                log.error(Throwables.getStackTraceAsString(e));
                semaphore.release();
            }
        });

        if(indexes.size() == 0) {
            queueService.add("Done");
        }

        Long end = (System.currentTimeMillis()-start)/1000;
        log.info(date+":"+hour+" takes seconds "+end+", and process cells "+countSize.get()+", data in size(M) "+String.format("%.2f", image.get()));

    }

    public void getDataByDateFeed(String system, String date, QueueService<String> queueService){

        Semaphore semaphore = new Semaphore(120);
        long start = System.currentTimeMillis();
        AtomicInteger countSize = new AtomicInteger();
        AtomicDouble image = new AtomicDouble();

        Set<String> indexes = redisTemplate.boundSetOps("date"+":"+system + ":" + date).members();
        log.info(date+" async-index size "+indexes.size());
        indexes.stream().forEach( index -> {
            String[] keys = index.split("#");
            try {
                semaphore.acquire();
                Flux<AoiEntity> aoiEntityFlux = aoiRepository.findByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinute(
                        date, keys[0], Integer.parseInt(keys[1]), Integer.parseInt(keys[2]));
                aoiEntityFlux.collectList().subscribe(s -> {
                    countSize.addAndGet(s.size());
                    s.stream().forEach(entity -> {
                        long imageSize = entity.getImage().array().length;
                        image.addAndGet((double)imageSize/1000000);
                        queueService.add(date+"@"+entity.getFileName());
                    });
                    semaphore.release();
                });
            }catch (Exception e){
                log.error(Throwables.getStackTraceAsString(e));
                semaphore.release();
            }
        });

        if(indexes.size() == 0) {
            queueService.add("Done");
        }

        Long end = (System.currentTimeMillis()-start)/1000;
        log.info(date+" takes seconds "+end+", and process cells "+countSize.get()+", data in size(M) "+String.format("%.2f", image.get()));
    }

    //ReadAction
    public void getData(DateDevice dateDevice){

        Semaphore semaphore = new Semaphore(60);

        String date = dateDevice.getDate();
        Set<String> deviceTypes = dateDevice.getDeviceTypes();
        int i = dateDevice.getHour();

        Iterator<String> itr = deviceTypes.iterator();

        while (itr.hasNext()) {

            try {
                String deviceType = itr.next();
                for (int x = 0; x < 60; x++) {
                    semaphore.acquire();
                    Flux<AoiEntity> aoiEntityFlux = aoiRepository.findByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinute(date, deviceType, i, x);
                    aoiEntityFlux.collectList().subscribe(s -> {
                        semaphore.release();
                        int size = s.size();
                        if(size > 0) {
                            WriteToFile.writeToFile(s);
                            String key = "created_day-" + date + ":device_type-" + deviceType + ":hour-" + i;
                            log.info(key + " size: " + size);
                        }
                    });
                }
            }catch (Exception e){
                log.error(Throwables.getStackTraceAsString(e));
                semaphore.release();
            }
        }

    }

    //ReadByDatesAction
    public void getDates(DateDevice dateDevice){

        Semaphore semaphore = new Semaphore(60);

        String date = dateDevice.getDate();
        Set<String> indexes = dateDevice.getIndexes();

        indexes.stream().forEach( next -> {
            try {
                String[] index = next.split("#");
                semaphore.acquire();

                Flux<AoiEntity> aoiEntityFlux = aoiRepository.findByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinute
                        (date, index[0], Integer.parseInt(index[1]), Integer.parseInt(index[2]));
                aoiEntityFlux.collectList().subscribe(s -> {
                    int size = s.size();
                    WriteToFile.writeToFile(s);
                    String key = "createdDay-"+date+":deviceType-"+index[0]+":hour-"+index[1]+":minute-"+index[2];
                    log.info(key + " size: " + size);
                });

                semaphore.release();
            }catch (Exception e){
                log.error(Throwables.getStackTraceAsString(e));
                semaphore.release();
            }
        });
    }

}
