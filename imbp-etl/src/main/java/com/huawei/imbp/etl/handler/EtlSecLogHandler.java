package com.huawei.imbp.etl.handler;

import com.huawei.imbp.etl.common.ImbpCommon;
import com.huawei.imbp.etl.entity.ActionEntity;
import com.huawei.imbp.etl.entity.IndexResult;
import com.huawei.imbp.etl.entity.ResultEntity;
import com.huawei.imbp.etl.service.SecLogService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @author Charles(Li) Cai
 * @date 4/16/2019
 */

@Component
@Log4j2
public class EtlSecLogHandler {

    @Autowired
    public SecLogService secLogService;

    public Mono<ServerResponse> getSecLogIndex(ServerRequest serverRequest){

        List<IndexResult> results = secLogService.getIndexMap();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(results);
    }

    public Mono<ServerResponse> getSecLogSize(ServerRequest serverRequest) {

        Map<String, Integer> size = secLogService.getSize();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(size);

    }

    public Mono<ServerResponse> updateSecLogData(ServerRequest serverRequest){

        return serverRequest.bodyToMono(ActionEntity.class).flatMap( s -> {
                ResultEntity result = secLogService.updateDataById(s);
                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(result);
            }
        );
    }

    public Mono<ServerResponse> replaySecLogData(ServerRequest serverRequest) {

        ResultEntity result;
        Optional<String> all = serverRequest.queryParam("all");

        if(all.isPresent()){
            result = secLogService.replayAllData();
        }else{
            Optional<String> data = serverRequest.queryParam("id");
            String[] ids = data.get().split(",");
            result = secLogService.replayDataById(ids);
        }

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(result);
    }

    public Mono<ServerResponse> deleteSecLogData(ServerRequest serverRequest) {

        ResultEntity result;
        Optional<String> all = serverRequest.queryParam("all");

        if(all.isPresent()){
            result = secLogService.deleteAllData();
        }else{
            Optional<String> ids = serverRequest.queryParam("id");
            String[] data = ids.get().split(",");
            result = secLogService.deleteDataById(data);
        }

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(result);
    }

    public Mono<ServerResponse> getSecLogData(ServerRequest serverRequest) {

        Optional<String> all = serverRequest.queryParam("all");
        if(all.isPresent()){
            List<ActionEntity> entities = secLogService.getSecData();
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(entities);
        }else {
            Optional<String> id = serverRequest.queryParam("id");
            if(!id.isPresent()) {
                ResultEntity error = new ResultEntity();
                error.setStatus(ImbpCommon.FAIL);
                error.setMessage("specify id or all parameter");
                return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).syncBody(error);
            }
            String[] ids = id.get().split(",");
            List<ActionEntity> obj = secLogService.getDataById(ids);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).syncBody(obj);
        }
    }

}
