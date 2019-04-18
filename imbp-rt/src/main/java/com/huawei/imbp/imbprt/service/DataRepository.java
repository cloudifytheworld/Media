package com.huawei.imbp.imbprt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.imbprt.config.ImbpEtlActionExtension;
import com.huawei.imbp.imbprt.util.OffHeapMemoryAllocation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 3/25/2019
 */

@Component
@Log4j2
@RefreshScope
public class DataRepository {

    @Autowired
    private ImbpEtlActionExtension imbpEtlActionExtension;

    @Autowired
    public Session session;

    @Autowired
    public OffHeapMemoryAllocation offHeapMemoryAllocation;

    @Autowired
    public ActorSystem actorSystem;
    public ActorRef fileAction;


    @PostConstruct
    public void init(){
        fileAction = actorSystem.actorOf(imbpEtlActionExtension.props("fileAction"));
    }
}
