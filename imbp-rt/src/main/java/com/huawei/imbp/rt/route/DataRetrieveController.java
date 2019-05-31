package com.huawei.imbp.rt.route;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.FeedEntity;
import com.huawei.imbp.rt.service.DataTransferService;
import com.huawei.imbp.rt.service.QueueService;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.stream.IntStream;

/**
 * @author Charles(Li) Cai
 * @date 5/16/2019
 */

@RestController
@Log4j2
@RefreshScope
public class DataRetrieveController {

    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    @Autowired
    public DataTransferService transferService;

    @Value("${data.rateLimit}")
    public int rateLimit;

    @Value("${data.sleepLimit}")
    public int sleepLimit;

    @GetMapping(value = "/api/{system}/rt/feeding", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> retrieveDataByFeeding(@RequestParam String from){

        long start = System.currentTimeMillis();
        ActorRef feedingAction = actorSystem.actorOf(imbpRtActionExtension.props("feedAction"));
        QueueService<String> queueService = new QueueService<>();
        String[] dateParam = from.split(",");
        FeedEntity<String> feedEntity = new FeedEntity<>();
        feedEntity.setQueue(queueService);
        feedEntity.setDate(dateParam[0]);
        if(dateParam.length>1){
            feedEntity.setHour(dateParam[1]);
        }else{
            queueService.setSleepLimit(sleepLimit);
        }
        feedEntity.setSystem("aoi");

        feedingAction.tell(feedEntity, ActorRef.noSender());
        queueService.waitForValue();

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

    @GetMapping(value = "/api/{system}/rt/file")
    public String retrieveDataByFile(@PathVariable String system, @RequestParam String start, @RequestParam String end){

        String groupId = transferService.generateFile(system, start, end);
        return groupId;
    }
}
