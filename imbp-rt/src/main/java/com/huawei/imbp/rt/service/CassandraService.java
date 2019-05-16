package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.datastax.driver.core.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.huawei.imbp.rt.common.InputParameter;
import com.huawei.imbp.rt.config.ImbpEtlActionExtension;
import com.huawei.imbp.rt.entity.Aoi;
import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.entity.AoiKey;
import com.huawei.imbp.rt.entity.DateDevice;
import com.huawei.imbp.rt.repository.AoiRepository;
import com.huawei.imbp.rt.util.*;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * @author Charles(Li) Cai
 * @date 4/14/2019
 */

@Component
@Log4j2
public class CassandraService{

    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public ImbpEtlActionExtension imbpEtlActionExtension;

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AoiRepository aoiRepository;


    public Mono<ServerResponse> getOneData(InputParameter input){

       Mono<AoiEntity> data = aoiRepository.findByKeyCreatedDayAndKeyDeviceType(input.getFrom()[0], input.getDeviceType());
       return ServerResponse.ok().body(BodyInserters.fromPublisher(data, AoiEntity.class));
    }

    public void getDataByDate(String system, String from){

        String[] dates = from.split(",");

        for(int i=0; i<dates.length; i++) {
            String date = dates[i].trim();
            log.info(date);
            Set<String> deviceTypes = redisTemplate.boundSetOps(system + ":" + date).members();
            for(int y=0; y<13; y++) {
                DateDevice dateDevice = new DateDevice();
                dateDevice.setDate(date);
                dateDevice.setDeviceTypes(deviceTypes);
                dateDevice.setHour(y);
                ActorRef readAction = actorSystem.actorOf(imbpEtlActionExtension.props("readAction"));
                readAction.tell(dateDevice, ActorRef.noSender());
            }
        }
    }


    public Mono<ServerResponse> getAoiPageData(InputParameter input, Pageable pageable){


        Flux<AoiEntity> results = aoiRepository.findAllByKeyCreatedDayAndKeyDeviceType(input.getFrom()[0], input.getDeviceType());
//        results.subscribe(s -> {
//            CassandraPageRequest next = (CassandraPageRequest)s.getPageable();
//            log.info(next.getPagingState().toString());
//            log.info(s.hasNext());
//            System.out.println(s.getContent());
//        });
        return results.collectList().flatMap(l -> {
            //List<AoiEntity> entities = l.getContent();
            Gson gson = new Gson();
            String json = gson.toJson(l);
           return ServerResponse.ok().body(Mono.just(json), String.class);

        });

    }


}
