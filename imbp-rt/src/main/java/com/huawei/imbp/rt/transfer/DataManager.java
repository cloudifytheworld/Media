package com.huawei.imbp.rt.transfer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.huawei.imbp.rt.common.JobStatus;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */

@Log4j2
public class DataManager {


    JobStorage storage;

    private RestTemplate restTemplate = new RestTemplate();
    JsonParser parser = new JsonParser();
    private List<String> clientServers = new ArrayList<>();
    private final String serverUrl = "http://localhost:8500/v1/catalog/service/imbp-rt";
    private final String serverStatusUrl = "http://localhost:8500/v1/agent/health/service/id/";
    private final String clientUri = "/api/system/rt/client/";

    public DataManager(JobStorage storage){
        this.storage = storage;
    }
    public void clear(String uuid){
        storage.remove(uuid);
    }

    public int getClients() {
        processClient();
        return clientServers.size();
    }

    public void processClient(){

        String rs = restTemplate.getForObject(serverUrl, String.class);

        JsonArray data = (JsonArray)parser.parse(rs);
        data.forEach( d -> {
            String address = d.getAsJsonObject().get("Address").getAsString();
            String serviceId = d.getAsJsonObject().get("ServiceID").getAsString();
            String port = d.getAsJsonObject().get("ServicePort").getAsString();
            getServerStatus(serviceId, address + ":" + port);
        });
    }

    private void getServerStatus(final String serviceId, final String address){

        try {
            String url = serverStatusUrl + serviceId;
            String status = restTemplate.getForObject(url, String.class);
            if (status.equals("passing")) {
                clientServers.add(address);
            }
        }catch (Exception e){
            log.info(serviceId+" "+e.getMessage());
        }
    }

    public void call(final ClientData clientData, String clientIp){

        String url = "http://"+clientIp+clientUri;
        String status = restTemplate.postForObject(url, clientData, String.class);
        log.info(status);
    }

    public void execute(final String system, String start, String end, String serverIp, String groupId){

        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime startDate = dtf.parseDateTime(start);
        DateTime endDate = dtf.parseDateTime(end);
        int serverSize = clientServers.size();
        DateTime nextDate = startDate;
        int y = 0;
        while(DateTimeComparator.getDateOnlyInstance().compare(nextDate, endDate) <= 0){

            y= y<serverSize?y:serverSize%y;
            String clientIp = clientServers.get(y);
            String clientId = UUID.randomUUID().toString();
            ClientData clientData = new ClientData();
            clientData.setClientId(clientId);
            clientData.setClientIp(clientIp);
            clientData.setServerIp(serverIp);
            clientData.setGroupId(groupId);
            clientData.setSystem(system);
            clientData.setStartDate(nextDate.toString(dtf));
            storage.put(groupId, clientId, clientData);
            call(clientData, clientIp);

            ++y;
            nextDate = nextDate.plusDays(1);
        }


    }
}
