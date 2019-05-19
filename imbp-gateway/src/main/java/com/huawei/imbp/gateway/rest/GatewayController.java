package com.huawei.imbp.gateway.rest;

import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
@Log4j2
@RefreshScope
public class GatewayController {

    @Autowired
    public WebClient.Builder webClient;

    @Value("${url.rt-page}")
    public String rtUrl;

    @Value("${url.etl-aoi}")
    public String aoiUrl;

    @Value("${url.etl-aoi-large}")
    public String aoiUrlLarge;


    @Value("${url.metadata-generate}")
    public String metadataUrl;

    @PostMapping("/{system}/etl")
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

    @PostMapping("/{system}/etl/large")
    public Mono<String> aoiEtlLargeService(@RequestBody Map body){

        return webClient.build().post()
                .uri(aoiUrlLarge)
                .body(Mono.just(body), Map.class)
                .exchange().flatMap(s -> s.bodyToMono(String.class));


    }

    @GetMapping(value = "/t/rt/feeding", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> feeding(@RequestParam String data){
        return webClient.baseUrl(rtUrl).build().get()
                .uri("/api/ws/rt/feeding?from="+data).accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve().bodyToFlux(String.class);
       // return Flux.fromStream(Stream.generate(() -> Math.random()+"@")).delayElements(Duration.ofMillis(50));

    }


    @ExceptionHandler(WebClientResponseException.class)
    public Mono<String> handleWebClientResponseException(WebClientResponseException ex) {
        String error = ex.getResponseBodyAsString();
        log.error("Error from iMBP gateway - Status {}, Body {}", ex.getRawStatusCode(),
                error, ex);
        return Mono.just(error);
    }
}
