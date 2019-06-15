package com.huawei.imbp.gateway.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;


@RestController
@RequestMapping("/api")
@Log4j2
@RefreshScope
public class GatewayController {

    @Autowired
    public WebClient.Builder webClient;

    @Value("${url.rt}")
    public String rtUrl;

    @Value("${url.etl}")
    public String etlUrl;

    @Value("${url.etl-large}")
    public String etlLargeUrl;

    @Value("${url.admin}")
    public String adminUrl;


    /*
     * ETL APIs
     */

    @PostMapping("/{system}/etl")
    public Mono<String> aoiEtlService(@PathVariable String system, @RequestBody Map body){

        return webClient.baseUrl(etlUrl).build().post()
                .uri("/api/"+system+"/etl")
                .body(Mono.just(body), Map.class)
                .exchange().flatMap(s -> s.bodyToMono(String.class));


    }

    @PostMapping("/{system}/etl/large")
    public Mono<String> aoiEtlLargeService(@PathVariable String system, @RequestBody Map body){

        return webClient.baseUrl(etlLargeUrl).build().post()
                .uri("/api/"+system+"/etl/large")
                .body(Mono.just(body), Map.class)
                .exchange().flatMap(s -> s.bodyToMono(String.class));
    }


    /*
     * RT APIs
     */

    @GetMapping(value = "/{system}/rt/download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<String> etlLargeService(@PathVariable String system, @PathVariable String id){

        return webClient.baseUrl(rtUrl).build().get()
                .uri("/api/"+system+"/rt/download/"+id)
                .exchange().flatMap(s -> s.bodyToMono(String.class));
    }


    @GetMapping(value = "/ws/rt/feeding", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> feeding(@RequestParam String data){
        return webClient.baseUrl(rtUrl).build().get()
                .uri("/api/ws/rt/feeding?from="+data).accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve().bodyToFlux(String.class);
    }


    /*
     * Admin APIs
     */

    @PostMapping("/admin/metadata/db")
    public Mono<String> metadataGeneration(){

        return webClient.baseUrl(adminUrl).build().post()
                .uri("/api/admin/metadata/db")
                .retrieve().bodyToMono(String.class);
    }

}
