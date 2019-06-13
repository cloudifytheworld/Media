package com.huawei.imbp.rt.handler;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.ClientData;
import com.huawei.imbp.rt.service.CassandraAsyncService;
import com.huawei.imbp.rt.service.QueueService;
import com.huawei.imbp.rt.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * @author Charles(Li) Cai
 * @date 6/13/2019
 */

@Component
@Log4j2
@RefreshScope
public class RtDataServiceHandler {


    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    @Autowired
    public CassandraAsyncService service;

    @Value("${data.rateLimit}")
    public int rateLimit;

    @Value("${data.sleepLimit}")
    public int sleepLimit;

    public Publisher<String> feedingDate(String system, DateTime startTime, DateTime endTime, boolean range){

        long start = System.currentTimeMillis();
        ActorRef feedingAction = actorSystem.actorOf(imbpRtActionExtension.props("feedAction"));
        CountDownLatch valueLatch = new CountDownLatch(1);
        QueueService<String> queueService = new QueueService<>(sleepLimit);
        String from = DataUtil.toDateString(startTime);

        ClientData clientData = new ClientData();
        clientData.setDateTimeRange(range);
        clientData.setQueue(queueService);
        clientData.setValueLatch(valueLatch);
        clientData.setDate(from);
        clientData.setStartTime(startTime.getMillis());
        clientData.setEndTime(endTime.getMillis());
        clientData.setSystem(system);


        try {
            feedingAction.tell(clientData, ActorRef.noSender());
            valueLatch.await();
            long ready = (System.currentTimeMillis() - start)/1000;
            log.info(" it takes "+ready+" seconds to be ready to start feeding data of "+from);
        }catch (Exception e){
            log.error(e);
        }

        return Flux.fromStream(queueService.asStream())
                .delayElements(Duration.ofMillis(rateLimit))
                .parallel().runOn(Schedulers.parallel())
                .doOnTerminate(() ->{
                    log.info(from+" is done on RT in mins "+ String.format("%.2f", (float)(System.currentTimeMillis()-start)/60000));
                }).doOnError(throwable -> {
                    log.error("+++++++++++RT++++++++++");
                    log.error(throwable);
                });
    }

}