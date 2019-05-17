package com.huawei.imbp.rt.route;

import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.service.CassandraService;
import org.joda.time.DateTime;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

/**
 * @author Charles(Li) Cai
 * @date 5/16/2019
 */

@RestController
public class DataFeedingController {

    @Autowired
    private CassandraService cassandraService;

    @GetMapping(value = "/api/{system}/rt/feeding", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> retrieveDataByFeeding(@RequestParam String from){
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        for(int i=0; i<10; i++) {
            try {
                Thread.sleep(1000);
            }catch (Exception e){}
            queue.add(new DateTime().toDate().toString());
        }

        //return cassandraService.getDataByFeeding(system, from);
        return Flux.fromStream(Stream.generate(() -> queue.poll())).delayElements(Duration.ofSeconds(1));
    }
}
