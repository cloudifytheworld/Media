package com.huawei.imbp.rt.route;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.FeedingEntity;
import com.huawei.imbp.rt.service.QueueService;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.stream.Stream;

/**
 * @author Charles(Li) Cai
 * @date 5/16/2019
 */

@RestController
@Log4j2
public class DataFeedingController {

    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    @GetMapping(value = "/api/{system}/rt/feeding", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> retrieveDataByFeeding(@RequestParam String from){

        long start = System.currentTimeMillis()/1000;
//        ActorRef feedingAction = actorSystem.actorOf(imbpRtActionExtension.props("feedingAction"));
//        QueueService<String> queueService = new QueueService<>();
//
//        FeedingEntity<String> feedingEntity = new FeedingEntity<>();
//        feedingEntity.setQueue(queueService);
//        feedingEntity.setDate(from);
//        feedingEntity.setSystem("aoi");
//
//        feedingAction.tell(feedingEntity, ActorRef.noSender());
//        while(queueService.size() == 0){};
//        return Flux.fromStream(Stream.generate(() ->  queueService.poll())).delayElements(Duration.ofMillis(50))
//                .onTerminateDetach().log("done in seconds "+(System.currentTimeMillis()/1000-start));
        return Flux.fromStream(Stream.generate(() -> from+"@"+ Math.random()).limit(200)).delayElements(Duration.ofMillis(50))
                .parallel().doOnComplete(() ->{
                    //log.info(queueService.getCount()+" items sent");
                    log.info("done in seconds "+(System.currentTimeMillis()/1000-start));
                });


    }
}
