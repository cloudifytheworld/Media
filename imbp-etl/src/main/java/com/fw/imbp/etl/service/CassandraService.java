package com.fw.imbp.etl.service;

import com.datastax.driver.core.querybuilder.Insert;
import com.google.common.base.Throwables;
import com.fw.imbp.etl.build.BuildStatement;
import com.fw.imbp.etl.build.StatementBuildFactory;
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

    @Autowired
    private StatementBuildFactory build;


    public Mono<ServerResponse> onProcess(final Map payload, final String system) throws Exception {

        log.debug("starting  ingestion process for " + system);

        try {
            BuildStatement buildStatement = build.get(system);
            Insert insert = buildStatement.build(payload, system);
            String key = buildStatement.buildIndex(redisTemplate);

            cassandraDataTemplate.getReactiveCqlOperations().execute(insert)
                    .doOnError( e ->
                        loggingService.onFailure(e, system, payload)
                    ).subscribe(
                            success -> log.debug("Done insertion on "+system+" for index "+key),
                            error -> log.debug("Fail to insert system "+system+" for index " +
                                    key+" on Error "+error)
            );
            return Mono.empty();
        } catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
            loggingService.onFailure(e, system, payload);
            throw e;
        }
    }
 }
