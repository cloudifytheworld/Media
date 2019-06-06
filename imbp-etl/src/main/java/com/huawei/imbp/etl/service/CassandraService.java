package com.huawei.imbp.etl.service;

import com.datastax.driver.core.querybuilder.Insert;
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
 * @date 5/15/2019
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



    public Mono<ServerResponse> onAoiProcess(final Map payload, final String system) throws Exception {

        //Todo all tables should be in configuration in consul
        final String keySpace = "images";
        final String table = "aoi_single_component_image_1";

        log.debug("starting  ingestion process for " + system);


        try {
            Insert insert = ConversionData.buildStatement(payload, system, keySpace, table);
            ConversionData.buildIndex(redisTemplate);

            cassandraDataTemplate.getReactiveCqlOperations().execute(insert)
                    .doOnError( e ->
                        loggingService.onFailure(e, system, payload)
                    ).subscribe(
                            success -> log.debug("Done insertion on "+system+" for index " +
                                    ConversionData.onComplete()),
                            error -> log.debug("Fail to insert system "+system+" for index " +
                                    ConversionData.onComplete()+" on Error "+error)
            );
            return Mono.empty();
        } catch (Exception e) {
            loggingService.onFailure(e, system, payload);
            throw e;
        }

    }
 }
