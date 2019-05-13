package com.huawei.imbp.etl.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 2/25/2019
 */

@Component
@RefreshScope
@Log4j2
public class EtlMetaDataHandler {

    @Value("#{${metadata.data}}")
    public Map<String, Map<String, Map<String, Object>>> metadata;

    public Mono<ServerResponse> getMetadata(ServerRequest serverRequest) {

        log.debug("Getting metaData in ETL");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(metadata), Map.class);
    }
}
