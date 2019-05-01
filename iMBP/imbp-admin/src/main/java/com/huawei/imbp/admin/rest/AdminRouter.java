package com.huawei.imbp.admin.rest;


import com.huawei.imbp.admin.service.MetaDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
/**
 * @author Charles(Li) Cai
 * @date 2/25/2019
 */

@Configuration
public class AdminRouter {

    @Bean
    public RouterFunction<ServerResponse> adminRouting(MetaDataService metaDataHandler){

        return RouterFunctions.route()
                .path("/api/admin/metadata", r -> r
                .nest(accept(MediaType.APPLICATION_JSON), m -> m
                        .POST("/db", metaDataHandler::handleMetadataDB)
                        .POST("/topic", metaDataHandler::handleMetadataTopic)
                        .POST("/hive", metaDataHandler::handleMetadataHive)
                )).build();
    }
}
