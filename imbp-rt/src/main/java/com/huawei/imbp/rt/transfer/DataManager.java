package com.huawei.imbp.rt.transfer;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.web.client.RestTemplate;

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
    private final String groupId;

    public DataManager(JobStorage storage){
        this.storage = storage;
        this.groupId = UUID.randomUUID().toString();
    }
    public void clear(String uuid){
        storage.remove(uuid);
    }

    public DataManager prepareClient(){

        String rs = restTemplate.getForObject(serverUrl, String.class);

        JsonArray data = (JsonArray)parser.parse(rs);
        data.forEach( d -> {
            String address = d.getAsJsonObject().get("Address").getAsString();
            String serviceId = d.getAsJsonObject().get("ServiceID").getAsString();
            String port = d.getAsJsonObject().get("ServicePort").getAsString();
            getServerStatus(serviceId, address + ":" + port);
        });

        return this;
    }

    //Todo filter out master server if more than one in clientServer list.
    private void getServerStatus(final String serviceId, final String address){

        try {
            String url = serverStatusUrl + serviceId;
            String status = restTemplate.getForObject(url, String.class);
            if (status.equals("passing")) {
                clientServers.add(address);
            }
        }catch (Exception e){
            log.warn(serviceId+" "+e.getMessage());
        }
    }

//    public void call(final ClientData clientData, String clientIp){
//
//        String url = "http://"+clientIp+clientUri;
//        String status = restTemplate.postForObject(url, clientData, String.class);
//        log.info(status);
//    }

    public void call(final String groupId){

        Map<String, ClientData> clients = storage.get(groupId);

        clients.forEach((k, clientData) -> {
            String clientIp = clientData.getClientIp();
            String url = "http://"+clientIp+clientUri;
            String status = restTemplate.postForObject(url, clientData, String.class);
            log.info(status);
        });

    }

    public void prepareCalls(final String system, String start, String end, String serverIp, int serverPort){

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
            clientData.setServerPort(serverPort);
            clientData.setGroupId(this.groupId);
            clientData.setSystem(system);
            clientData.setStartDate(nextDate.toString(dtf));
            storage.put(this.groupId, clientId, clientData);

            ++y;
            nextDate = nextDate.plusDays(1);
        }
    }

    public String getGroupId(){

        return this.groupId;

    }
}
