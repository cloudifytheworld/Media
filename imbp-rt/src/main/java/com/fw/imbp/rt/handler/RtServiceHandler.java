package com.fw.imbp.rt.handler;


import com.fw.imbp.rt.common.FeedType;
import com.fw.imbp.rt.common.ImbpException;
import com.fw.imbp.rt.common.JobStatus;
import com.fw.imbp.rt.service.CassandraReactiveService;
import com.fw.imbp.rt.service.CassandraAsyncService;
import com.fw.imbp.rt.service.DataTransferService;
import com.fw.imbp.rt.entity.ClientData;
import com.fw.imbp.rt.service.FeedDataService;
import com.fw.imbp.rt.util.DataUtil;
import com.fw.imbp.rt.util.Logging;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;


/**
 * @author Charles(Li) Cai
 * @date 04/09/2019
 */

@Component
public class RtServiceHandler {

    @Autowired
    public DataTransferService transferService;

    @Autowired
    public FeedDataService feedDataService;

    @Autowired
    public CassandraAsyncService cassandraAsyncService;

    @Autowired
    public CassandraReactiveService cassandraReactiveService;

    @Autowired
    public Logging log;


    /*
     * Require param: system, start, end date
     * ToDo: add project to all pathVariable other than system
     *
     */
    public Mono<ServerResponse> retrieveDataToFileByDate(ServerRequest serverRequest){

        String system = serverRequest.pathVariable("system");

        try {
            Optional<String> start = serverRequest.queryParam("start");
            Optional<String> end = serverRequest.queryParam("end");
            Optional<String> consolidation = serverRequest.queryParam("consolidate");

            if(!start.isPresent()) {
                throw new ImbpException().setMessage("missing start date");
            }

            DateTime startTime = DataUtil.convertDate(start.get());
            DateTime endTime = end.isPresent() && !end.get().equals(start.get())
                        ?DataUtil.convertDate(end.get()):startTime.plusDays(1).minusMillis(1);

            if(endTime.isBefore(startTime.getMillis())){
                throw new ImbpException().setMessage("end date is smaller than start date");
            }

            Boolean consolidate = consolidation.isPresent()?Boolean.parseBoolean(consolidation.get()):true;
            log.debug(String.format("startTime: %s - endTime: %s - consolidation: %s", startTime.toString(),
                    endTime.toString(), consolidate.toString()));

            Mono<String> groupId = transferService.processServer(system, startTime, endTime, consolidate, false);
            return ServerResponse.ok().body(groupId, String.class);
        }catch (Exception e){
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }
    }

    public Mono<ServerResponse> retrieveDataToFileByDateTime(ServerRequest serverRequest){

        String system = serverRequest.pathVariable("system");

        try {
            Optional<String> start = serverRequest.queryParam("startTime");
            Optional<String> end = serverRequest.queryParam("endTime");
            Optional<String> consolidation = serverRequest.queryParam("consolidate");

            if(!start.isPresent()){
                throw new ImbpException().setMessage("missing startTime");
            }

            DateTime startTime = DataUtil.convertDateTime(start.get());
            DateTime endTime = end.isPresent() && !end.get().equals(start.get())
                        ?DataUtil.convertDateTime(end.get()):DataUtil.endOfDateTime(startTime);

            if(endTime.isBefore(startTime.getMillis())){
                throw new ImbpException().setMessage("endTime is smaller than startTime");
            }

            Boolean consolidate = consolidation.isPresent()?Boolean.parseBoolean(consolidation.get()):true;
            log.debug(String.format("startTime: %s - endTime: %s - consolidation: %s", startTime.toString(),
                    endTime.toString(), consolidate.toString()));

            Mono<String> groupId = transferService.processServer(system, startTime, endTime, consolidate, true);
            return ServerResponse.ok().body(groupId, String.class);
        }catch (Exception e){
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }
    }

    public Mono<ServerResponse> processClient(ServerRequest serverRequest){

        log.debug("start to processClient");

        Mono<ServerResponse> response = serverRequest.bodyToMono(Map.class).flatMap(s -> {

            ClientData clientData = DataUtil.convertMapToObject(s, ClientData.class);
            transferService.processClient(clientData);
            clientData.setStatus(JobStatus.starting);
            return ServerResponse.ok().syncBody(clientData);
        });
        return response;
    }


    public Mono<ServerResponse> download(ServerRequest serverRequest){

        String id = serverRequest.pathVariable("id");
        log.debug("start to download file "+id);

        try {
            return transferService.processDownload(id);
        }catch (Exception e){
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }
    }

    public Mono<ServerResponse> feed(ServerRequest serverRequest){

        DateTime startTime;
        DateTime endTime;
        String from = serverRequest.queryParam("from").get();

        String[] data = from.split(":");
        if(data.length < 3){
            return ServerResponse.badRequest().syncBody(Flux.error(new ImbpException().setMessage("not enough values to process "+from)));
        }
        String system = data[0];
        FeedType type = FeedType.valueOf(data[1]);

        try {
            switch (type) {
                case date:
                    startTime = DataUtil.convertDate(data[2]);
                    endTime = data.length == 4   && !data[2].equals(data[3])
                            ? DataUtil.convertDate(data[3]) : startTime.plusDays(1).minusMillis(1);
                    break;
                case dateTime:
                    startTime = DataUtil.convertDateTime(data[2]);
                    endTime = data.length == 4  && !data[2].equals(data[3])
                            ?DataUtil.convertDateTime(data[3]):DataUtil.endOfDateTime(startTime);
                    break;
                default:
                    return ServerResponse.badRequest().syncBody(Flux.just("not support feed type - date or dateTime"));

            }

            if(endTime.isBefore(startTime.getMillis())){
                throw new ImbpException().setMessage("endTime is smaller than startTime");
            }
        }catch (Exception e){
            return ServerResponse.badRequest().syncBody(Flux.error(e));
        }

        return feedDataService.feedDate(system, startTime, endTime, type.equals(FeedType.dateTime));
    }
}

