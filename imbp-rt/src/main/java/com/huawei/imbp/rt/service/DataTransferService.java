package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.common.base.Throwables;
import com.huawei.imbp.rt.common.ImbpException;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.transfer.*;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */

@Component
@Log4j2
public class DataTransferService {

    @Value("${data.filePath}")
    public String filePath;

    @Autowired
    JobStorage storage;

    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    private ImbpException imbp = new ImbpException();

    public Mono<String> processServer(String system, DateTime start, DateTime end) throws Exception{

        DataManager dataManager = new DataManager(storage);
        NetworkManageService netService = new NetworkManageService();
        dataManager.prepareClient().prepareCalls(system, start, end,
                netService.getServerIp(), netService.getSocketPort());
        Integer participateClients = storage.groupSize(dataManager.getGroupId());
        if(participateClients == 0){
            throw imbp.setMessage("servers is not ready yet");
        }
        final CountDownLatch jobs = new CountDownLatch(participateClients);
        final CountDownLatch ready = new CountDownLatch(1);

        try {
            ActorRef serverAction = actorSystem.actorOf(imbpRtActionExtension.props("serverAction"));
            serverAction.tell(new ServerActionData(jobs, ready, dataManager,
                    netService.getSocketAddress(), participateClients, filePath), ActorRef.noSender());

            return Mono.just(dataManager.getGroupId())  ;

        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
            return Mono.just("fail: "+e.getMessage());
        }
    }

    public void processClient(ClientData clientData){

        ActorRef fileAction = actorSystem.actorOf(imbpRtActionExtension.props("fileAction"));
        fileAction.tell(clientData, ActorRef.noSender());

    }
}
