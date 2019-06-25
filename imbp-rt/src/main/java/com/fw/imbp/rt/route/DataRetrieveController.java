package com.fw.imbp.rt.route;


import com.fw.imbp.rt.common.FeedType;
import com.fw.imbp.rt.common.ImbpException;

import com.fw.imbp.rt.service.FeedDataService;

import com.fw.imbp.rt.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;



/**
 * @author Charles(Li) Cai
 * @date 5/16/2019
 */

@RestController
@Log4j2
@RequestMapping("/api/{system}/rt")
public class DataRetrieveController {

    @Autowired
    public FeedDataService handler;

    /*
     * Params: the format of from parameter is expected as following
     *        system:feedType:start:end
     * The feedType either date or dateTime
     * The start must be there, end is optional
     * The minimum value expected for from is 3
     */
    @GetMapping(value = "/feeding", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> retrieveDataByFeeding(@RequestParam String from){


        DateTime startTime;
        DateTime endTime;

        String[] data = from.split(":");
        if(data.length < 3){
            return Flux.error(new ImbpException().setMessage("not enough values to process "+from));
        }
        String system = data[0];
        FeedType type = FeedType.valueOf(data[1]);

        try {
            switch (type) {
                case date:
                    startTime = DataUtil.convertDate(data[2]);
                    endTime = data.length == 4   && !data[2].equals(data[3])
                            ? DataUtil.convertDate(data[3]) : startTime.plusDays(1).minusMillis(1);
                    break;
                case dateTime:
                    startTime = DataUtil.convertDateTime(data[2]);
                    endTime = data.length == 4  && !data[2].equals(data[3])
                            ?DataUtil.convertDateTime(data[3]):DataUtil.endOfDateTime(startTime);
                    break;
                default:
                    return Flux.just("not support feed type - date or dateTime");

            }

            if(endTime.isBefore(startTime.getMillis())){
                throw new ImbpException().setMessage("endTime is smaller than startTime");
            }
        }catch (Exception e){
            return Flux.error(e);
        }

        return handler.feedingDate(system, startTime, endTime, type.equals(FeedType.dateTime));
    }

}
