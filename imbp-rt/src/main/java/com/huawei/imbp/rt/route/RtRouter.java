package com.huawei.imbp.rt.route;

import com.huawei.imbp.rt.handler.RtServiceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public RouterFunction<ServerResponse> rtDataByDateRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/date"),
                rtServiceHandler::retrieveDataByDate);
    }

    @Bean
    public RouterFunction<ServerResponse> rtDataByDateRangeRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/date/range"),
                rtServiceHandler::retrieveDataByDate);
    }

    @Bean
    public RouterFunction<ServerResponse> rtDataFeedingRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/feeding"),
            rtServiceHandler::retrieveDataByDate);
    }

    @Bean
    public RouterFunction<ServerResponse> rtDataPaginationRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/page"),
                rtServiceHandler::retrieveDataByPagination);
    }

    @Bean
    public RouterFunction<ServerResponse> rtDataSavingRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/location"),
                rtServiceHandler::retrieveDataByDate);
    }
}