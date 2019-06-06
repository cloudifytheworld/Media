package com.huawei.imbp.etl.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.huawei.imbp.etl.entity.ActionEntity;
import com.huawei.imbp.etl.config.ImbpEtlActionExtension;
import com.huawei.imbp.etl.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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


    public void onFailure(final Throwable ex, final String system, final Map<String, Object> input){
        send(ex.getMessage(), system, input);
    }

    public void onRun(final List<Map<String, Object>> data){
        secRunAction.tell(data, ActorRef.noSender());
    }

    public void onFallback(final String msg, final String system, final Map<String, Object> payload){
            send(msg, system, payload);
    }

    @PostConstruct
    public void init(){
        logAction = actorSystem.actorOf(imbpEtlActionExtension.props("logAction"));
        secLogAction = actorSystem.actorOf(imbpEtlActionExtension.props("secLogAction"));
        secRunAction = actorSystem.actorOf(imbpEtlActionExtension.props("secRunAction"));
    }

    private void send(final String msg, final String system, final Map<String, Object> input){

        String id = DataUtil.createId(input);

        ActionEntity entity = new ActionEntity();
        entity.setId(id);
        entity.setSystem(system);
        entity.setErrorMsg(msg);
        entity.setInput(input);

        logAction.tell(msg, ActorRef.noSender());
        secLogAction.tell(entity, ActorRef.noSender());
    }
}


