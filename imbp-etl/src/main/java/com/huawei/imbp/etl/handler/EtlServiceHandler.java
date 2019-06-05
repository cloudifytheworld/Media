package com.huawei.imbp.etl.handler;

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

import static com.huawei.imbp.etl.common.ImbpCommon.AOI;

import java.util.Map;
import java.util.Set;

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

    public Mono<ServerResponse> handleEtlService(ServerRequest serverRequest){

        String system = serverRequest.pathVariable("system");
        if (StringUtils.isEmpty(system)) {
            return ServerResponse.badRequest().syncBody("must specify which system to ingest");
        }

        log.debug("handling imbp-etl service for "+system);

        Mono<ServerResponse> response = serverRequest.bodyToMono(Map.class).flatMap(s -> {

                try {

                    switch (system.toLowerCase()) {
                        case AOI:
                            return cassandraService.onAoiProcess(s, system);
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
                .commandName("ETL-process")
                .groupName("ETLGroup")
                .eager()
                .commandProperties(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(20)
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(20)
                        .withExecutionTimeoutInMilliseconds(timeout)
                )
                .fallback(loggingService.onFallback("short circuit triggered ", system, serverRequest, timeout))
                .toMono();

        return hystrixResponse;
    }

}
