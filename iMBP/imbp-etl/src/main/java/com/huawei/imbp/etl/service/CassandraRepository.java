package com.huawei.imbp.etl.service;

import com.datastax.driver.core.Statement;
import com.huawei.imbp.etl.service.build.BuildStatementService;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CassandraRepository extends DataRepository{


    @Autowired
    public BuildStatementService buildStatementService;

    public Mono<ServerResponse> onInsert(Map rawData) throws Exception{

        log.debug("enter onInsert");

        Statement statement = buildStatementService.buildCassandraOnInsert(rawData);
        execute(statement, rawData);

        return Mono.empty();
    }

    public Mono<ServerResponse> onInsertLarge(Map rawData) throws Exception{

        long start = System.currentTimeMillis();
        log.debug("enter onInsert Large");

        Statement statement = buildStatementService.buildCassandraOnInsertLarge(rawData);
        execute(statement, rawData);

        log.debug("it takes " + (System.currentTimeMillis() - start));

        return Mono.empty();
    }
  }

