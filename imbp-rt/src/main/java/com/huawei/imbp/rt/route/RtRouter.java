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

    /*
     * Require params: system, from, deviceType
     * Optional: hour, minutes, label and created_day for first run, subsequently access to
     *           next page requires these parameters.
     * ToDo:  1. support from (start day) to begin only
     *        2. support from and to (end day) to begin
     *        3. support deviceType including 1 and 2 above.
     */
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

    /*
     * Require params: system, from(start day) and deviceType
     * Todo: support label and created_time
     */
    @Bean
    public RouterFunction<ServerResponse> rtDataSingleRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/single"),
                rtServiceHandler::retrieveDataSingle);
    }

    @Bean
    public RouterFunction<ServerResponse> rtDataFileRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/file"),
                rtServiceHandler::retrieveDataByFile);
    }

    @Bean
    public RouterFunction<ServerResponse> rtDataClientRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/{system}/rt/client"),
                rtServiceHandler::processClient);
    }
}