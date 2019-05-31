package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.common.base.Throwables;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.transfer.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
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

    public Mono<String> processServer(String system, String from, String end){

        DataManager dataManager = new DataManager(storage);
        int servers = dataManager.getClients();
        CountDownLatch jobs = new CountDownLatch(servers);
        CountDownLatch ready = new CountDownLatch(1);

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress,9500);
            int port = inetSocketAddress.getPort();
            log.info("server socket port "+ port);
            DataReceiver dataReceiver = new DataReceiver(inetSocketAddress, servers, filePath);

            ActorRef serverAction = actorSystem.actorOf(imbpRtActionExtension.props("serverAction"));
            serverAction.tell(new ServerData(jobs, ready, dataReceiver, dataManager), ActorRef.noSender());

            try {
                ready.wait();
            }catch (Exception e){
                log.error(e.getMessage());
            }
            String serverIp = inetAddress.getHostAddress()+":"+port;
            dataManager.execute(system, from, end, serverIp, dataReceiver.getGroupId());

            dataManager.clear(dataReceiver.getGroupId());
            dataReceiver.close();
            return Mono.just(dataReceiver.getGroupId());

        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
            return Mono.just("fail: "+e.getMessage());
        }
    }

    public String generateFile(String system, String from, String end){

        DataManager dataManager = new DataManager(storage);
        int servers = dataManager.getClients();
        final CountDownLatch jobs = new CountDownLatch(servers);
        final CountDownLatch ready = new CountDownLatch(1);

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress,9500);
            int port = inetSocketAddress.getPort();
            log.info("server socket port "+ port);
            DataReceiver dataReceiver = new DataReceiver(inetSocketAddress, servers, filePath);

            ActorRef serverAction = actorSystem.actorOf(imbpRtActionExtension.props("serverAction"));
            serverAction.tell(new ServerData(jobs, ready, dataReceiver, dataManager), ActorRef.noSender());

            ready.wait();
            String serverIp = inetAddress.getHostAddress()+":"+port;
            dataManager.execute(system, from, end, serverIp, dataReceiver.getGroupId());

            dataManager.clear(dataReceiver.getGroupId());
            dataReceiver.close();
            return dataReceiver.getGroupId();

        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
            return "fail: "+e.getMessage();
        }
    }

    public void processClient(Map clientData){

        ActorRef fileAction = actorSystem.actorOf(imbpRtActionExtension.props("fileAction"));
        fileAction.tell(clientData, ActorRef.noSender());

    }
}
