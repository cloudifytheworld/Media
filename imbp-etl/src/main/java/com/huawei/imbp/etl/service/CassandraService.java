package com.huawei.imbp.etl.service;

import com.google.common.base.Throwables;
import com.huawei.imbp.etl.repository.AoiRepository;
import com.huawei.imbp.etl.entity.AoiEntity;
import com.huawei.imbp.etl.transform.ConversionData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.StringJoiner;

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
    private AoiRepository aoiRepository;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<ServerResponse> onAoiProcess(final Map requestData) {

        log.debug("handling aoi insertion process");
        Map<String, Object> payload = (Map)requestData.get("payload");

        try {
            AoiEntity entity = ConversionData.convert(payload);
            aoiRepository.insert(entity).subscribe(s -> { log.debug("insertion is successful");});
            createIndex(entity, (String)requestData.get("sender"));
            return Mono.empty();
        }catch (Exception e){
            loggingService.onFailure(e, payload);
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }
    }


    private void createIndex(AoiEntity entity, String system ){

        try {
            String key = entity.getKey().getDeviceType()
                    + "#" + entity.getKey().getHour() + "#" + entity.getKey().getMinute();
            String fileKey = entity.getKey().getCreatedDay() + "#" + key + "#" + entity.getKey().getLabel() + "#" + entity.getKey().getCreatedTime();

            redisTemplate.opsForSet().add(system + ":" + entity.getKey().getCreatedDay(), key).subscribe();
            redisTemplate.opsForSet().add("fileName"+":"+entity.getFileName(), fileKey).subscribe();
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
        }
    }
 }
