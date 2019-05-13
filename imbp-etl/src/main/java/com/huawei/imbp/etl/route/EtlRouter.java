package com.huawei.imbp.etl.route;

import com.huawei.imbp.etl.handler.EtlMetaDataHandler;
import com.huawei.imbp.etl.handler.EtlSecLogHandler;
import com.huawei.imbp.etl.handler.EtlServiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * @author Charles(Li) Cai
 * @date 2/15/2019
 */

@Configuration
public class EtlRouter {

    @Bean
    public RouterFunction<ServerResponse> aoiEtlRouting(EtlServiceHandler etlServiceHandler) {

        return RouterFunctions.route(RequestPredicates.POST("/api/{aoi}/etl").
                        and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                etlServiceHandler::handleEtlService);
    }


    @Bean
    public RouterFunction<ServerResponse> etlMetaData(EtlMetaDataHandler etlMetaDataHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/etl/metadata").
                        and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                etlMetaDataHandler::getMetadata);
    }


    @Bean
    public RouterFunction<ServerResponse> etlLargeRouting(EtlServiceHandler etlServiceHandler) {

        return RouterFunctions.route()
                .path("/api", r -> r
                        .nest(accept(MediaType.APPLICATION_JSON), m -> m
                                .POST("/etl/large", etlServiceHandler::handleEtlLargeService)
                                .POST("/{aoi}/etl/large", etlServiceHandler::handleEtlLargeService)
                        )).build();
    }
}