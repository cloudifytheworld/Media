package com.huawei.imbp.imbprt.handler;


import com.huawei.imbp.imbprt.service.CassandraService;
import com.huawei.imbp.imbprt.util.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;


/**
 * @author Charles(Li) Cai
 * @date 04/09/2019
 */

@Component
public class RtServiceHandler {


    @Autowired
    public CassandraService cassandraService;

    @Autowired
    public Logging log;



    public Mono<ServerResponse> retrieveDateService(ServerRequest serverRequest) {

        log.debug("retrieve data service");
        Optional<String> created_day = serverRequest.queryParam("created_day");
        Optional<String> device_type = serverRequest.queryParam("device_type");
        Optional<String> from = serverRequest.queryParam("from");
        Optional<String> to = serverRequest.queryParam("to");
        //cassandraService.getData(created_day.get(), device_type.get(), from.get(), to.get());
        cassandraService.getData(from.get(), to.get());
        return Mono.empty();
    }

}

