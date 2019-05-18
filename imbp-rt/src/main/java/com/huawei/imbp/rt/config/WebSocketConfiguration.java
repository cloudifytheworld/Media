package com.huawei.imbp.rt.config;

import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.route.DataFeedingController;
import com.huawei.imbp.rt.service.CassandraService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 5/16/2019
 */

@Configuration
public class WebSocketConfiguration {

    @Bean
    SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler webSocketHandler){
        return new SimpleUrlHandlerMapping(){
            {
                setOrder(1);
                Map<String, WebSocketHandler> map = new HashMap<>();
                map.put("/ws/api/rt/feeding", webSocketHandler);
                setUrlMap(map);
            }
        };
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter(){
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(DataFeedingController queue){
        return new WebSocketHandler() {
            @Override
            public Mono<Void> handle(WebSocketSession webSocketSession) {

                Flux<String> ask =  webSocketSession.receive().map(WebSocketMessage::getPayloadAsText);
                Flux<String> response =  ask.flatMap(queue::retrieveDataByFeeding);
                Flux<WebSocketMessage> rep = response.map(data -> webSocketSession.textMessage(data));
                return webSocketSession.send(rep);
            }
        };

    }
}
