package com.huawei.imbp.gateway.config;

import com.huawei.imbp.gateway.rest.WebSocketController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 5/17/2019
 */

@Configuration
public class WebSocketConfiguration {

    @Bean
    SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler webSocketHandler){
        return new SimpleUrlHandlerMapping(){
            {
                setOrder(10);
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
    WebSocketHandler webSocketHandler(WebSocketController controller) {
        return session -> {
            Flux<String> ask = session.receive().map(WebSocketMessage::getPayloadAsText);
            Flux<String> response = ask.flatMap(controller::feeding);
            Flux<WebSocketMessage> rep = response.map(data -> session.textMessage(data));
            return session.send(rep);
        };
    }
}
