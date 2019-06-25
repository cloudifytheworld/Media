package com.fw.imbp.etl.route;

import com.fw.imbp.etl.handler.EtlSecLogHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @author Charles(Li) Cai
 * @date 4/15/2019
 */

@Configuration
public class SecRouter {


    @Bean
    public RouterFunction<ServerResponse> getSecLogIndex(EtlSecLogHandler etlSecLogHandler){

        return RouterFunctions.route(RequestPredicates.GET("/api/etl/sec/index"),
                etlSecLogHandler::getSecLogIndex);

    }

    @Bean
    public RouterFunction<ServerResponse> getSecLogSize(EtlSecLogHandler etlSecLogHandler){

        return RouterFunctions.route(RequestPredicates.GET("/api/etl/sec/size"),
                etlSecLogHandler::getSecLogSize);
    }

    @Bean
    public RouterFunction<ServerResponse> resetSecLogSize(EtlSecLogHandler etlSecLogHandler){

        return RouterFunctions.route(RequestPredicates.PATCH("/api/etl/sec/replay"),
                etlSecLogHandler::replaySecLogData);
    }

    @Bean
    public RouterFunction<ServerResponse> deleteSecLogData(EtlSecLogHandler etlSecLogHandler){

        return RouterFunctions.route(RequestPredicates.DELETE("/api/etl/sec/data"),
                etlSecLogHandler::deleteSecLogData);
    }

    @Bean
    public RouterFunction<ServerResponse> getSecLogData(EtlSecLogHandler etlSecLogHandler){

        return RouterFunctions.route(RequestPredicates.GET("/api/etl/sec/data"),
                etlSecLogHandler::getSecLogData);

    }

    @Bean
    public RouterFunction<ServerResponse> updateSecLogData(EtlSecLogHandler etlSecLogHandler){

        return RouterFunctions.route(RequestPredicates.PUT("/api/etl/sec/data"),
                etlSecLogHandler::updateSecLogData);

    }
}
