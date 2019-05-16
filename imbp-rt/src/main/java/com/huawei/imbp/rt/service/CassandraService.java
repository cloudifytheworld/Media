package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.gson.Gson;
import com.huawei.imbp.rt.common.InputParameter;
import com.huawei.imbp.rt.config.ImbpEtlActionExtension;
import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.entity.DateDevice;
import com.huawei.imbp.rt.repository.AoiRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

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


    public Mono<ServerResponse> getAoiPageData(InputParameter input){
        Flux<AoiEntity> results = null;

        if(input.getCreatedTime() == null) {
            results = aoiRepository.findTop2ByKeyCreatedDayAndKeyDeviceType(input.getFrom()[0], input.getDeviceType());
        }else{
            results = aoiRepository.findTop2ByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinuteAndKeyLabelAndKeyCreatedTimeLessThan(
                    input.getFrom()[0], input.getDeviceType(), input.getHour(), input.getMinute(), input.getLabel(), input.getCreatedTime()
            );
        }

        return results.collectList().flatMap(l -> {
            Map<String, Object> result = new HashMap<>();
            if(l.size() == 0){
                result.put("status", "no data");
                return ServerResponse.ok().syncBody(result);
            }
            result.put("status", "success");
            result.put("input", l);
            result.put("previous", l.get(0).getKey());
            result.put("next", l.get(l.size()-1).getKey());
            return ServerResponse.ok().body(BodyInserters.fromObject(result));

        });

    }


}
