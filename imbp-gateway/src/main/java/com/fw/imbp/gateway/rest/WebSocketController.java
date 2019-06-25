package com.fw.imbp.gateway.rest;

import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author Charles(Li) Cai
 * @date 5/18/2019
 */

@RefreshScope
@RestController
@Log4j2
public class WebSocketController {

    @Autowired
    public WebClient.Builder webClient;

    @Value("${url.rt}")
    public String rtUrl;

    public Publisher<String> feeding(@RequestParam String data){

        long start = System.currentTimeMillis();
        return webClient.baseUrl(rtUrl).build().get()
                .uri("/api/ws/rt/feeding?from="+data).accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve().bodyToFlux(String.class)
                .parallel().runOn(Schedulers.parallel())
                .doOnTerminate(() -> {
                    log.info(data+" completed on Gateway seconds "+(System.currentTimeMillis()-start)/1000);
                }).doOnError(t -> {
                    log.error("----------GateWay------------");
                    log.error(data+" fail to execute or complete "+t.getMessage());
                });

    }
}
