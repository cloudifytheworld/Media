package com.huawei.imbp.gateway.config;

import com.huawei.imbp.gateway.rest.WebSocketController;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @author Charles(Li) Cai
 * @date 5/17/2019
 */

@Configuration
@Log4j2
public class WebSocketConfig {

    private final Set<String> sessions = new HashSet<>();

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler webSocketHandler){
        return new SimpleUrlHandlerMapping(){
            {
                setOrder(10);
                Map<String, WebSocketHandler> map = new HashMap<>();
                map.put("/ws/api/rt/feeding", webSocketHandler);
                map.put("/ws/api/rt/feed", webSocketHandler);
                setUrlMap(map);
            }
        };
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter(){
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public WebSocketHandler webSocketHandler(WebSocketController controller) {
        return session -> {
            final String sessionId = session.getId();
            if(sessions.add(sessionId)) {
                log.info("--Start webSocket session id " + session.getId());
                Flux<String> ask = session.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doFinally(s -> {
                            log.info("--Terminate webSocket session client sig {}, id {}", s.name(), sessionId);
                            session.close();
                            sessions.remove(sessionId);
                        });
                Flux<String> response = ask.flatMap(controller::feeding);
                Flux<WebSocketMessage> rep = response.map(data -> session.textMessage(data));
                return session.send(rep);
            }
            return Mono.empty();
        };
    }
}
