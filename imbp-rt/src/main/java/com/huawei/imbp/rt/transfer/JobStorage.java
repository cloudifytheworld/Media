package com.huawei.imbp.rt.transfer;

import com.huawei.imbp.rt.common.JobStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Charles(Li) Cai
 * @date 5/28/2019
 */

@Component
@Log4j2
public class JobStorage {

    private final ConcurrentMap<String, Map<String, ClientData>> jobStore = new ConcurrentHashMap<>();

    public void put(String groupId, String clientId, ClientData clientData){

        Map<String, ClientData> clientDataMap = jobStore.get(groupId);
        if(clientDataMap == null){
            clientDataMap = new HashMap<>();
        }

        clientDataMap.put(clientId, clientData);

    }

    public void setClientStatus(String groupId, String clientId, JobStatus jobStatus){

        Map<String, ClientData> clientDataMap = jobStore.get(groupId);
        ClientData  clientData = clientDataMap.get(clientId);
        clientData.setStatus(jobStatus);
    }

    public void remove(String uuid){
        jobStore.remove(uuid);
    }
}
