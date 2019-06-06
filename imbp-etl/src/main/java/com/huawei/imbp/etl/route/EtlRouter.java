package com.huawei.imbp.etl.route;

import com.huawei.imbp.etl.handler.EtlServiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;


/**
 * @author Charles(Li) Cai
 * @date 2/15/2019
 */

@Configuration
public class EtlRouter {

    @Bean
    public RouterFunction<ServerResponse> aoiEtlRouting(EtlServiceHandler etlServiceHandler) {

        return RouterFunctions.route(RequestPredicates.POST("/api/{system}/etl").
                        and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                etlServiceHandler::handleEtlService);
    }

}