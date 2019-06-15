package com.huawei.imbp.rt.route;

import com.huawei.imbp.rt.handler.RtServiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @author Charles(Li) Cai
 * @date 04/08/2019
 */

@Configuration
public class RtRouter {

    @Bean
    public RouterFunction<ServerResponse> rtDataPaginationRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/page"),
                rtServiceHandler::retrieveDataToFileByDate);
    }


    @Bean
    public RouterFunction<ServerResponse> rtDataSingleRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/download/{id}"),
                rtServiceHandler::download);
    }

    @Bean
    public RouterFunction<ServerResponse> rtDataToFileByDateRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/file/date"),
                rtServiceHandler::retrieveDataToFileByDate);
    }

    @Bean
    public RouterFunction<ServerResponse> rtDataToFileByDateTimeRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/file/dateTime"),
                rtServiceHandler::retrieveDataToFileByDateTime);
    }

    @Bean
    public RouterFunction<ServerResponse> rtDataClientRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.POST("/api/{system}/rt/client").
                        and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                rtServiceHandler::processClient);
    }
}