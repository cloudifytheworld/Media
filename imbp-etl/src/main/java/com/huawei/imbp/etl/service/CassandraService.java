package com.huawei.imbp.etl.service;

import com.netflix.hystrix.HystrixCommandProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.HystrixCommands;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.huawei.imbp.etl.common.ImbpCommon.OK;
import static com.huawei.imbp.etl.common.ImbpCommon.FALLBACK;

/**
 * @author Charles(Li) Cai
 * @date 2/15/2019
 */

@Component
@RefreshScope
@Log4j2
public class CassandraService {

    @Value("${request.timeout}")
    public int timeout;

    @Autowired
    public LoggingService loggingService;

    @Autowired
    public CassandraRepository cassandraRepository;

    public Mono<ServerResponse> onProcess(final Map requestData) {

        log.debug("enter CassandraService handling process");

        Mono<String> result = HystrixCommands.from(Mono.just(OK))
                .commandName("ETL-process")
                .groupName("ETLGroup")
                .eager()
                .commandProperties(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionTimeoutInMilliseconds(timeout)
                )
                .fallback(Mono.just(FALLBACK)).toMono();

        return result.flatMap( s -> {
            switch(s){
                case FALLBACK:
                    loggingService.onFailure("request timeout", requestData);
                    break;
                case  OK:
                    try {
                        return cassandraRepository.onInsert(requestData);
                    }catch (Exception e){
                        loggingService.onFailure(e, requestData);
                        return ServerResponse.badRequest().syncBody(e.getMessage());
                    }
            }
            return Mono.empty();
        });
    }

    public Mono<ServerResponse> onLargeProcess(final Map requestData) {

        try{
            cassandraRepository.onInsertLarge(requestData);
        }catch (Exception e){
            loggingService.onFailure(e, requestData);
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }
        return Mono.empty();
    }

 }
