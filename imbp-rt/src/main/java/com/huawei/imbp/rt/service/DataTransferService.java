package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.common.base.Throwables;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.transfer.ClientData;
import com.huawei.imbp.rt.transfer.DataManager;
import com.huawei.imbp.rt.transfer.DataReceiver;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

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
    public ActorSystem actorSystem;

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    public Mono<String> processServer(String system, String from, String end){

        DataManager dataManager = new DataManager();
        int servers = dataManager.getClients();
        InetSocketAddress inetAddress = new InetSocketAddress(0);
        int port = inetAddress.getPort();
        log.info("server socket port "+port);
        DataReceiver dataReceiver = new DataReceiver(inetAddress, servers, filePath);
        try {
            dataReceiver.run(dataManager);
            String serverIp = inetAddress.getAddress().getHostAddress()+":"+port;
            dataManager.execute(system, from, end, serverIp, dataReceiver.getGroupId());
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
        }
        return Mono.just(dataReceiver.getGroupId());
    }


    public void processClient(ClientData clientData){

        ActorRef fileAction = actorSystem.actorOf(imbpRtActionExtension.props("fileAction"));
        fileAction.tell(clientData, ActorRef.noSender());

    }
}
