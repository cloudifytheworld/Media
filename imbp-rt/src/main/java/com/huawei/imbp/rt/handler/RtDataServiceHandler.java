package com.huawei.imbp.rt.handler;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.FeedData;
import com.huawei.imbp.rt.entity.ClientDateTime;
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
import java.util.List;
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

        try {
            
            List<ClientDateTime> dateTimes = DataUtil.getDateTimes(startTime, endTime);
            
            FeedData feedData = new FeedData();
            feedData.setDateTimeRange(range);
            feedData.setQueue(queueService);
            feedData.setValueLatch(valueLatch);
            feedData.setDateTimes(dateTimes);
            feedData.setSystem(system);

            log.debug("the feed data "+feedData.toString());
    
            feedingAction.tell(feedData, ActorRef.noSender());
            valueLatch.await();
            long ready = (System.currentTimeMillis() - start)/1000;
            log.debug(" it takes "+ready+" seconds to be ready to start feeding data of "+from);
        }catch (Exception e){
            return Flux.error(e);
        }

        return Flux.fromStream(queueService.asStream())
                .delayElements(Duration.ofMillis(rateLimit))
                .parallel().runOn(Schedulers.parallel())
                .doOnTerminate(() ->{
                    log.debug(from+" is done on RT in mins "+ String.format("%.2f", (float)(System.currentTimeMillis()-start)/60000));
                }).doOnError(throwable -> {
                    log.error("+++++++++++RT++++++++++");
                    log.error(throwable);
                });
    }
}
