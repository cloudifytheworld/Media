package com.huawei.imbp.gateway.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Log4j2
@RefreshScope
public class GatewayController {

    @Autowired
    public WebClient.Builder webClient;

    @Value("${url.etl-aoi}")
    public String aoiUrl;

    @Value("${url.etl-aoi-large}")
    public String aoiUrlLarge;


    @Value("${url.metadata-generate}")
    public String metadataUrl;

    @PostMapping("/{aoi}/etl")
    public Mono<String> aoiEtlService(@RequestBody Map body){

        return webClient.build().post()
                .uri(aoiUrl)
                .body(Mono.just(body), Map.class)
                .exchange().flatMap(s -> s.bodyToMono(String.class));


    }

    @PostMapping("/admin/metadata/db")
    public Mono<String> metadataGeneration(){

        return webClient.build().post()
                .uri(metadataUrl)
                .retrieve().bodyToMono(String.class);
    }


    @PostMapping("/etl/large")
    public Mono<String> etlLargeService(@RequestBody Map body){

        return webClient.build().post()
                .uri(aoiUrlLarge)
                .body(Mono.just(body), Map.class)
                .exchange().flatMap(s -> s.bodyToMono(String.class));


    }

    @PostMapping("/{aoi}/etl/large")
    public Mono<String> aoiEtlLargeService(@RequestBody Map body){

        return webClient.build().post()
                .uri(aoiUrlLarge)
                .body(Mono.just(body), Map.class)
                .exchange().flatMap(s -> s.bodyToMono(String.class));


    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<String> handleWebClientResponseException(WebClientResponseException ex) {
        String error = ex.getResponseBodyAsString();
        log.error("Error from iMBP gateway - Status {}, Body {}", ex.getRawStatusCode(),
                error, ex);
        return Mono.just(error);
    }
}
