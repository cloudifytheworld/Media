package com.huawei.imbp.etl.service;

import com.google.common.base.Throwables;
import com.huawei.imbp.etl.entity.AoiEntity;
import com.huawei.imbp.etl.transform.ConversionData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;



import java.util.Map;


/**
 * @author Charles(Li) Cai
 * @date 2/15/2019
 */

@Component
@Log4j2
public class CassandraService {

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private ReactiveCassandraOperations cassandraDataTemplate;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<ServerResponse> onAoiProcess(final Map requestData) {

        log.debug("handling aoi insertion process");
        Map<String, Object> payload = (Map)requestData.get("payload");

        try {
            AoiEntity entity = ConversionData.convert(payload);
            cassandraDataTemplate.insert(entity).subscribe(s -> { log.debug("insertion is successful");});
            createIndex(entity, (String)requestData.get("sender"));
            return Mono.empty();
        }catch (Exception e){
            loggingService.onFailure(e, payload);
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }
    }


//    private void createIndex(AoiEntity entity, String system ){
//
//        try {
//            String dateKey = entity.getKey().getDeviceType()
//                    + "#" + entity.getKey().getHour() + "#" + entity.getKey().getMinute();
//            String hourKey = entity.getKey().getDeviceType() +"#" + entity.getKey().getMinute();
//            String primaryKey = entity.getKey().getDeviceType() +"#" + entity.getKey().getMinute()
//                    + "#" + entity.getKey().getLabel() + "#" + entity.getKey().getCreatedTime();
//
//            redisTemplate.opsForSet().add("date"+":"+system+":"+entity.getKey().getCreatedDay(), dateKey).subscribe();
//            redisTemplate.opsForSet().add("hour"+":"+system+":"+entity.getKey().getCreatedDay()+":"+entity.getKey().getHour(), hourKey).subscribe();
//            redisTemplate.opsForSet().add("primary"+":"+system+":"+entity.getKey().getCreatedDay()+":"+entity.getKey().getHour(), primaryKey).subscribe();
//            redisTemplate.opsForSet().add("device"+":"+system+":"+entity.getKey().getCreatedDay(), entity.getKey().getDeviceType()).subscribe();
//        }catch (Exception e){
//            log.error(Throwables.getStackTraceAsString(e));
//        }
//    }
//    private void createIndex(AoiEntity entity, String system ){
//
//        try {
//            String dateKey = entity.getKey().getDeviceType()
//                +"#"+entity.getKey().getHour()+"#"+entity.getKey().getMinute()+"#"+entity.getKey().getSecond();
//            String hourKey = entity.getKey().getDeviceType() +"#" + entity.getKey().getMinute()+"#"+entity.getKey().getSecond();;
//            String primaryKey = entity.getKey().getDeviceType() +"#" + entity.getKey().getMinute()+"#"+entity.getKey().getSecond()
//                + "#" + entity.getKey().getLabel() + "#" + entity.getKey().getCreatedTime();
//
//            redisTemplate.opsForSet().add("secDate"+":"+system+":"+entity.getKey().getCreatedDay(), dateKey).subscribe();
//            redisTemplate.opsForSet().add("secHour"+":"+system+":"+entity.getKey().getCreatedDay()+":"+entity.getKey().getHour(), hourKey).subscribe();
//            redisTemplate.opsForSet().add("secPrimary"+":"+system+":"+entity.getKey().getCreatedDay()+":"+entity.getKey().getHour(), primaryKey).subscribe();
//        }catch (Exception e){
//            log.error(Throwables.getStackTraceAsString(e));
//        }
//    }
    private void createIndex(AoiEntity entity, String system ){

        long mills =  entity.getKey().getCreatedTime().getTime();
        try {
//            String dateKey = entity.getKey().getDeviceType()
//                +"#"+entity.getKey().getHour()+"#"+entity.getKey().getMinute()+"#"+entity.getKey().getSecond()
//                    + "#" + entity.getKey().getLabel() + "#" + entity.getKey().getCreatedTime();
//            String hourKey = entity.getKey().getDeviceType()+"#"+entity.getKey().getHour()+"#" + entity.getKey().getMinute()+"#"+entity.getKey().getSecond();;
            String primaryKey = entity.getKey().getDeviceType()+"#"+entity.getKey().getHour() +"#" + entity.getKey().getMinute()+"#"+entity.getKey().getSecond()
                + "#" + entity.getKey().getLabel() + "#" + mills;


            redisTemplate.opsForZSet().add("secDate"+":"+system+":"+entity.getKey().getCreatedDay(),
                    primaryKey, mills).subscribe();
            redisTemplate.opsForZSet().add("secHour"+":"+system+":"+entity.getKey().getCreatedDay()+":"+entity.getKey().getHour(),
                    primaryKey, mills).subscribe();
            redisTemplate.opsForZSet().add("secDevice"+":"+system+":"+entity.getKey().getDeviceType(),
                    primaryKey, mills).subscribe();
            redisTemplate.opsForZSet().add("secTime"+":"+system+":"+mills,
                    primaryKey, mills).subscribe();
            redisTemplate.opsForZSet().add("secDeviceTime"+":"+system+":"+mills+":"+entity.getKey().getDeviceType(),
                    primaryKey, mills).subscribe();

    }catch (Exception e){
        log.error(Throwables.getStackTraceAsString(e));
    }
}
 }
