package com.huawei.imbp.etl.handler;

import com.huawei.imbp.etl.service.CassandraService;
import com.huawei.imbp.etl.service.LoggingService;
import com.huawei.imbp.etl.util.Logging;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.huawei.imbp.etl.common.ImbpCommon.CASSANDRA;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 2/15/2019
 */

@Component
public class EtlServiceHandler {

    @Autowired
    public LoggingService loggingService;

    @Autowired
    public CassandraService cassandraService;

    @Autowired
    public Logging log;

    public Mono<ServerResponse> handleEtlService(ServerRequest serverRequest){

        log.debug("handle etl service");

        return serverRequest.bodyToMono(Map.class).flatMap(s -> {

                String destination = (String)s.get("destination");
                try {
                    if (isDestinationEmpty(destination, s)) {
                        return ServerResponse.badRequest().syncBody("destination empty");
                    }
                    switch (destination.toLowerCase()) {
                        case CASSANDRA:
                            return cassandraService.onProcess(s);
                        default:
                            return ServerResponse.badRequest().syncBody(destination + "not supported");
                    }
                }catch (Exception e){
                    return ServerResponse.badRequest().syncBody(e.getMessage());
                }
            }
        );
    }


    private boolean isDestinationEmpty(String destination, Map<String, Object> input) {


        if(StringUtils.isEmpty(destination)){
            loggingService.onFailure("destination empty", input);
            return true;
        }

        return false;
    }
}
