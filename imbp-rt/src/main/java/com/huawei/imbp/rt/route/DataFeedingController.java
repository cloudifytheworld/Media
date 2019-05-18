package com.huawei.imbp.rt.route;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.entity.FeedingEntity;
import com.huawei.imbp.rt.service.QueueService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Stream;

/**
 * @author Charles(Li) Cai
 * @date 5/16/2019
 */

@RestController
public class DataFeedingController {

    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    @GetMapping(value = "/api/{system}/rt/feeding", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> retrieveDataByFeeding(@RequestParam String from){

        ActorRef feedingAction = actorSystem.actorOf(imbpRtActionExtension.props("feedingAction"));
        QueueService<String> queueService = new QueueService<>();

        FeedingEntity<String> feedingEntity = new FeedingEntity<>();
        feedingEntity.setQueue(queueService);
        feedingEntity.setDate(from);
        feedingEntity.setSystem("aoi");

        feedingAction.tell(feedingEntity, ActorRef.noSender());
        while(queueService.size() == 0){};
        return Flux.fromStream(Stream.generate(() ->  queueService.poll())).delayElements(Duration.ofMillis(50));


    }
}
