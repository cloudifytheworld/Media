package com.huawei.imbp.rt.handler;


import com.google.common.base.Throwables;
import com.huawei.imbp.rt.common.InputParameter;

import com.huawei.imbp.rt.common.JobStatus;
import com.huawei.imbp.rt.service.CassandraAsyncService;
import com.huawei.imbp.rt.service.CassandraThreadedService;
import com.huawei.imbp.rt.service.DataTransferService;
import com.huawei.imbp.rt.transfer.ClientData;
import com.huawei.imbp.rt.util.DataUtil;
import com.huawei.imbp.rt.util.ServiceUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Client;

import java.util.Map;
import java.util.Optional;


/**
 * @author Charles(Li) Cai
 * @date 04/09/2019
 */

@Component
@Log4j2
public class RtServiceHandler {

    @Autowired
    public DataTransferService transferService;

    @Autowired
    public CassandraThreadedService cassandraThreadedService;

    @Autowired
    public CassandraAsyncService cassandraAsyncService;

    /*
     * Require params: system, from(start day) and deviceType
     * ToDo: with list of deviceType
     */
    public Mono<ServerResponse> retrieveDataSingle(ServerRequest serverRequest) {

        log.debug("retrieve single data");

        try {
            InputParameter input = ServiceUtil.getInputParam(serverRequest);
            return cassandraAsyncService.getDataByOne(input);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }

    }

    /*
     * Require params: system, from(start day) or list of start day.
     * Todo: Results should save to location or somewhere
     */
    public Mono<ServerResponse> retrieveDataByDate(ServerRequest serverRequest) {

        log.debug("retrieve data by date");

        try {
            InputParameter input = ServiceUtil.getInputParam(serverRequest);
            //cassandraService.getDataByDates(input);
            cassandraThreadedService.getDataByDates(input);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }
        return Mono.empty();
    }

    /*
     * Require params: system, from, deviceType
     * Optional: hour, minutes, label and created_day for first run, subsequently access to
     *           next page requires these parameters.
     * ToDo:  1. support from (start day) to begin only
     *        2. support from and to (end day) to begin
     *        3. support deviceType including 1 and 2 above.
     */

    public Mono<ServerResponse> retrieveDataByPagination(ServerRequest serverRequest){

        try{
            InputParameter input = ServiceUtil.getInputParam(serverRequest);
            return cassandraAsyncService.getDataByPage(input);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }
    }

    /*
     * Require param: system, start, end date
     * ToDo: add project to all pathVariable other than system
     *
     */
    public Mono<ServerResponse> retrieveDataByFile(ServerRequest serverRequest){

        String system = serverRequest.pathVariable("system");
        Optional<String> start = serverRequest.queryParam("start");
        Optional<String> end = serverRequest.queryParam("end");

        try {
            Mono<String> groupId = transferService.processServer(system, start.get(), end.get());
            return ServerResponse.ok().body(groupId, String.class);
        }catch (Exception e){
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }
    }

    public Mono<ServerResponse> processClient(ServerRequest serverRequest){

        Mono<ServerResponse> response = serverRequest.bodyToMono(Map.class).flatMap(s -> {

            ClientData clientData = DataUtil.convertMapToObject(s, ClientData.class);
            transferService.processClient(clientData);
            clientData.setStatus(JobStatus.starting);
            return ServerResponse.ok().syncBody(clientData);
        });
        return response;
    }
}

