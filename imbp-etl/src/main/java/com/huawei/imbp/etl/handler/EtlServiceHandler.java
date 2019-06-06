package com.huawei.imbp.etl.handler;

import com.huawei.imbp.etl.build.OnSystem;
import com.huawei.imbp.etl.service.CassandraService;
import com.huawei.imbp.etl.service.LoggingService;
import com.huawei.imbp.etl.util.Logging;
import com.netflix.hystrix.HystrixCommandProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.HystrixCommands;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 2/15/2019
 */

@Component
@RefreshScope
@EnableCircuitBreaker
public class EtlServiceHandler {

    @Autowired
    public LoggingService loggingService;

    @Autowired
    public CassandraService cassandraService;

    @Autowired
    public Logging log;

    @Value("${request.timeout}")
    public int timeout;

    public Mono<ServerResponse> handleEtlService(final ServerRequest serverRequest){

        final String system = serverRequest.pathVariable("system");
        if (StringUtils.isEmpty(system)) {
            return ServerResponse.badRequest().syncBody("must specify which system to ingest in url");
        }


        Mono<ServerResponse> response = serverRequest.bodyToMono(Map.class).flatMap(input -> {
                log.debug("handling imbp-etl service for "+system);
                try {

                    switch (OnSystem.valueOf(system.toLowerCase())) {
                        case aoi:
                            return cassandraService.onAoiProcess(input, system);
                        default:
                            return ServerResponse.badRequest().syncBody(system + "is not supported");
                    }
                }catch (Exception e){
                    return ServerResponse.badRequest().syncBody(e.getMessage());
                }
            }
        );

        Mono<ServerResponse> hystrixResponse = HystrixCommands
                .from(response)
                .commandName(system+"-"+"ETL-Process")
                .groupName(system+"-"+"ETL-Group")
                .eager()
                .commandProperties(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(20)
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(20)
                        .withExecutionTimeoutInMilliseconds(timeout)
                )
                .fallback((OnFallBack) -> {
                    return onFallbackCall(system, serverRequest);
                })
                .toMono();

        return hystrixResponse;
    }

    private Mono<ServerResponse> onFallbackCall(final String system, final ServerRequest serverRequest){

        final String errorMsg = system+" request is slower than defined timeout "+timeout;

        serverRequest.bodyToMono(Map.class).subscribe(payload -> {
            loggingService.onFallback(errorMsg, system, payload);
        });

        return ServerResponse.badRequest().syncBody(errorMsg);
    }

}
