package com.huwei.imbp.admin.rest;


import com.huwei.imbp.admin.service.MetaDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AdminRouter {

    @Bean
    public RouterFunction<ServerResponse> adminRouting(MetaDataService metaDataHandler){

        return RouterFunctions.route(RequestPredicates.POST("/api/admin/metadata/run").
                        and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                metaDataHandler::handleMetadata);
    }
}
