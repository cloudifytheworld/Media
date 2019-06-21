package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.common.base.Throwables;
import com.huawei.imbp.rt.common.ImbpException;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.ClientData;
import com.huawei.imbp.rt.transfer.*;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
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

    @Value("${server.port}")
    int port;

    @Autowired
    JobStorage storage;

    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    private ImbpException imbp = new ImbpException();

    public Mono<String> processServer(String system, DateTime start, DateTime end, boolean consolidate, boolean range) throws Exception{

        NetworkManageService netService = new NetworkManageService(port);
        DataManager dataManager = new DataManager(storage, netService);
        dataManager.prepareClient().prepareCalls(system, start, end, consolidate, range,
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
            //Todo save to mongoDB
            return Mono.just(dataManager.getGroupId()+"#"+netService.getServerIp()+"#"+port)  ;

        }catch (Exception e){
            log.error(e);
            return Mono.just("fail: "+e.getMessage());
        }
    }

    public void processClient(ClientData clientData){

        ActorRef fileAction = actorSystem.actorOf(imbpRtActionExtension.props("fileAction"));
        fileAction.tell(clientData, ActorRef.noSender());

    }


    public Mono<ServerResponse> processDownload(String id) throws Exception{

        InputStreamResource resource;
        File file = new File(filePath + id);

        try {
            resource = new InputStreamResource(new FileInputStream(file));
        }catch (Exception e){
            log.error(e);
            throw imbp.setMessage("fail to process file "+e.getMessage());
        }
        return ServerResponse.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + id)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(BodyInserters.fromObject(resource));
    }
}
