package com.huwei.imbp.admin.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RefreshScope
public class ServiceCallManager {

    @Value("${imbp.metadata.url}")
    public String metadataUrl;

    @Autowired
    public WebClient.Builder webClient;

    public Mono<String> pushToConsul(String metadata){

        return webClient.build().put()
                .uri(metadataUrl)
                .body(Mono.just(metadata), String.class)
                .retrieve().bodyToMono(String.class);

    }
}
