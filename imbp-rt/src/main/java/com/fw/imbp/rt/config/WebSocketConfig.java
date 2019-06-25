package com.fw.imbp.rt.config;

import com.fw.imbp.rt.route.DataRetrieveController;
import lombok.extern.log4j.Log4j2;
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
 * @date 5/16/2019
 */

@Configuration
@Log4j2
public class WebSocketConfig {

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
    WebSocketHandler webSocketHandler(DataRetrieveController controller){
        return webSocketSession ->  {
            log.info("Start webSocket session "+webSocketSession.getId());
            Flux<String> ask =  webSocketSession.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .doFinally( s -> {
                        log.info("Terminate webSocket session");
                        webSocketSession.close();
                    });
            Flux<String> response =  ask.flatMap(controller::retrieveDataByFeeding);
            Flux<WebSocketMessage> rep = response.map(data -> webSocketSession.textMessage(data));
            return webSocketSession.send(rep);
        };
    }
}
