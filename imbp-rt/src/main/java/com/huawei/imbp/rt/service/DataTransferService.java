package com.huawei.imbp.rt.service;

import com.google.common.base.Throwables;
import com.huawei.imbp.rt.transfer.DataManager;
import com.huawei.imbp.rt.transfer.DataReceiver;
import lombok.extern.log4j.Log4j2;
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

    public Mono<String> processMaster(String system, String from){

        DataManager dataManager = new DataManager();
        int servers = dataManager.getClients();
        InetSocketAddress inetAddress = new InetSocketAddress(9501);
        DataReceiver dataReceiver = new DataReceiver(inetAddress, servers, filePath);
        try {
            dataReceiver.run(dataManager);
            String masterIp = inetAddress.getAddress().getHostAddress();
            dataManager.call(masterIp, dataReceiver.getGroupId(), from);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
        }
        return Mono.empty();
    }


    public void processSlave(String system, String start){

    }
}
