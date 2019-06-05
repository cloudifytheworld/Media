package com.huawei.imbp.etl.service;

import com.datastax.driver.core.querybuilder.Insert;
import com.google.common.base.Throwables;
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



    public Mono<ServerResponse> onAoiProcess(final Map requestData, String system) {

        //Todo all tables should be in configuration in consul
        final String keySpace = "images";
        final String table = "aoi_single_component_image_1";

        log.debug("starting  ingestion process for " + system);

        Map<String, Object> payload = (Map) requestData.get("payload");

        try {
            Insert insert = ConversionData.buildStatement(payload, system, keySpace, table);
            ConversionData.buildIndex(redisTemplate);
            cassandraDataTemplate.getReactiveCqlOperations()
                    .execute(insert).subscribe(
                    s -> {
                        String index = ConversionData.onComplete();
                        log.debug("Done insertion on "+system+" for index "+index);
                    });
            return Mono.empty();
        } catch (Exception e) {
            String index = ConversionData.onComplete();
            log.debug("Fail to insert system "+system+" for index "+index+
                    "---"+Throwables.getStackTraceAsString(e));
            loggingService.onFailure(e, system, payload);
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }
    }
 }
