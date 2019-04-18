package com.huawei.imbp.imbprt.route;

import com.huawei.imbp.imbprt.handler.RtServiceHandler;
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
    public RouterFunction<ServerResponse> rtDateRouting(RtServiceHandler rtServiceHandler) {

        return RouterFunctions.route(RequestPredicates.GET("/api/aoi/rt"),
            rtServiceHandler::retrieveDateService);
    }

}