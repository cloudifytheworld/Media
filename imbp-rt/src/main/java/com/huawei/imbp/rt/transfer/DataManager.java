package com.huawei.imbp.rt.transfer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huawei.imbp.rt.common.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */


public class DataManager {

    @Autowired
    private WebClient.Builder webClient;

    @Autowired
    JobStorage storage;

    private Map<String, Boolean> clientServers = new HashMap<>();
    private final String serverUrl = "http://localhost:8500/v1/catalog/service/imbp-rt";
    private final String serverStatusUrl = "http://localhost:8500/v1/health/service/id/";
    private final String clientUri = "/api/system/rt/client/";
    Type type = new TypeToken<List<Map<String, String>>>() {}.getType();

    public void clear(String uuid){
        storage.remove(uuid);
    }

    public int getClients() {
        processClient();
        return clientServers.size();
    }

    public void processClient(){

        webClient.baseUrl(serverUrl)
                .build()
                .get()
                .retrieve().bodyToMono(String.class)
                .subscribe(s -> {
                    Gson gson = new Gson();
                    List<Map> data = gson.fromJson(s, type);
                    data.stream().forEach( d -> {
                        String address = (String)d.get("Address");
                        String serviceId = (String)d.get("ServiceID");
                        getServerStatus(serviceId).subscribe(status -> {
                            Map<String, String> dataStatus = gson.fromJson(status, Map.class);
                            String st = dataStatus.get("AggregatedStatus");
                            String port = dataStatus.get("port");
                            if(st.equals("passing")){
                                clientServers.put(address+":"+port, true);
                            }
                        });
                    });
                });

    }

    private Mono<String> getServerStatus(String serviceId){

        return webClient.baseUrl(serverStatusUrl+serviceId)
                .build()
                .get()
                .retrieve()
                .bodyToMono(String.class);
    }

    public void call(final String ip, final String groupId, String start){

        clientServers.forEach((k,v) -> {
            String clientId = UUID.randomUUID().toString();
            ClientData clientData = new ClientData();
            clientData.setIp(k);
            clientData.setStatus(JobStatus.Starting);
            storage.put(groupId, clientId, clientData);
            String url = "http://"+k+clientUri+"?ip="+ip+"&groupId="+groupId+"&start="+start+"&clientId="+clientId;

            webClient.baseUrl(url)
                    .build()
                    .get()
                    .retrieve().bodyToMono(String.class)
                    .subscribe(s ->{
                        JobStatus jobStatus = JobStatus.valueOf(s);
                        storage.setClientStatus(groupId, clientId, jobStatus);
                    });

        });
    }
}
