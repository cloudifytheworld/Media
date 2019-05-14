package com.huawei.imbp.etl.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.huawei.imbp.etl.entity.ActionEntity;
import com.huawei.imbp.etl.config.ImbpEtlActionExtension;
import com.huawei.imbp.etl.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 2/15/2019
 */

@Component
public class LoggingService {

    @Autowired
    private ImbpEtlActionExtension imbpEtlActionExtension;

    @Autowired
    private ActorSystem actorSystem;

    private ActorRef logAction;
    private ActorRef secLogAction;
    private ActorRef secRunAction;

    public Mono<String> onFailure(String msg, Map<String, Object> input){
        send(msg, input);
        return Mono.empty();
    }

    public void onFailure(Throwable ex, Map<String, Object> input){
        send(ex.getMessage(), input);
    }

    public void onRun(List<Map<String, Object>> data){
        secRunAction.tell(data, ActorRef.noSender());
    }

    public Mono<ServerResponse> onFallback(String msg, ServerRequest request){

        request.bodyToMono(Map.class).flatMap(s -> {
            send(msg, s);
            return ServerResponse.badRequest().syncBody("Save to SEC log");
        });

        return ServerResponse.badRequest().syncBody(msg+"request is slower than defined timeout");
    }
    @PostConstruct
    public void init(){
        logAction = actorSystem.actorOf(imbpEtlActionExtension.props("logAction"));
        secLogAction = actorSystem.actorOf(imbpEtlActionExtension.props("secLogAction"));
        secRunAction = actorSystem.actorOf(imbpEtlActionExtension.props("secRunAction"));
    }

    private void send(String msg, Map<String, Object> input){

        String id = DataUtil.createId(input);
        ActionEntity entity = new ActionEntity();
        entity.setId(id);
        entity.setErrorMsg(msg);
        entity.setInput(input);
        logAction.tell(msg, ActorRef.noSender());
        secLogAction.tell(entity, ActorRef.noSender());
    }
}


