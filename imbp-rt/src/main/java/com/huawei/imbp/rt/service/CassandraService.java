package com.huawei.imbp.rt.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.datastax.driver.core.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.rt.config.ImbpEtlActionExtension;
import com.huawei.imbp.rt.entity.Aoi;
import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.entity.AoiKey;
import com.huawei.imbp.rt.entity.DateDevice;
import com.huawei.imbp.rt.repository.AoiRepository;
import com.huawei.imbp.rt.util.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;

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


    public Object getAoiPageData(Object key, Pageable pageable){

        if(AoiKey.class.isInstance(key)) {
            Flux<Slice<AoiEntity>> results = aoiRepository.findByKey((AoiKey)key, pageable);
            return results;
        }

        return null;
    }
}
