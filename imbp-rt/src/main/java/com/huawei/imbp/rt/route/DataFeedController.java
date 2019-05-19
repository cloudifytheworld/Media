package com.huawei.imbp.rt.route;

import akka.actor.ActorSystem;
import com.huawei.imbp.rt.config.ImbpRtActionExtension;
import com.huawei.imbp.rt.service.QueueService;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.IntStream;

/**
 * @author Charles(Li) Cai
 * @date 5/16/2019
 */

@RestController
@Log4j2
public class DataFeedController {

    @Autowired
    public ActorSystem actorSystem;

    @Autowired
    public ImbpRtActionExtension imbpRtActionExtension;

    @GetMapping(value = "/api/{system}/rt/feeding", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> retrieveDataByFeeding(@RequestParam String from){

//        ActorRef feedingAction = actorSystem.actorOf(imbpRtActionExtension.props("feedAction"));
        QueueService<String> queueService = new QueueService<>();
//
//        FeedEntity<String> feedingEntity = new FeedEntity<>();
//        feedingEntity.setQueue(queueService);
//        feedingEntity.setDate(from);
//        feedingEntity.setSystem("aoi");
//
//        feedingAction.tell(feedEntity, ActorRef.noSender());
        IntStream.range(0, 2000).boxed().map(n -> String.valueOf(n)).forEach( s-> queueService.add(s));
        //queueService.waitForValue();

        long start = System.currentTimeMillis()/1000;

//        return Flux.fromStream(Stream.generate(() ->  queueService.poll())).delayElements(Duration.ofMillis(50))
//                .onTerminateDetach().log("done in seconds "+(System.currentTimeMillis()/1000-start));
        return Flux.fromStream(queueService.asStream())

       // return Flux.fromStream(Stream.generate(() -> from+"@"+ ((int)Math.random()*2000000)).limit(2000))
                .delayElements(Duration.ofMillis(1))
//                .parallel()
//                .runOn(Schedulers.parallel()).sequential()
                .doOnTerminate(() ->{
                    log.info(queueService.getCount()+" items sent");
                    log.info(from+" done in seconds "+(System.currentTimeMillis()/1000-start));
                }).doOnError(throwable -> {
                    log.error("+++++++++++RT++++++++++");
                    log.error(throwable);
                });


    }
}
