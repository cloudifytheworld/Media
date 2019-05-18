package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.datastax.driver.core.Session;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.util.OffHeapMemoryAllocation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Charles(Li) Cai
 * @date 3/25/2019
 */

@Component
@Log4j2
@RefreshScope
public class DataRepository {

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    @Autowired
    public Session session;

    @Autowired
    public OffHeapMemoryAllocation offHeapMemoryAllocation;

    @Autowired
    public ActorSystem actorSystem;
//    public ActorRef fileAction;
    public ActorRef readAction;
    @PostConstruct
    public void init(){
//        fileAction = actorSystem.actorOf(imbpEtlActionExtension.props("fileAction"));
        readAction = actorSystem.actorOf(imbpRtActionExtension.props("readAction"));

    }
}
