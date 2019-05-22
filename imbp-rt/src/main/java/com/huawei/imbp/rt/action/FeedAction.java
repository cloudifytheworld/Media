package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.huawei.imbp.rt.entity.FeedEntity;
import com.huawei.imbp.rt.service.CassandraAsyncService;
import com.huawei.imbp.rt.service.CassandraThreadedService;
import com.huawei.imbp.rt.service.QueueService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * @author Charles(Li) Cai
 * @date 3/13/2019
 */

@Component("feedAction")
@Log4j2
@Scope("prototype")
public class FeedAction extends UntypedAbstractActor {

    @Autowired
    private CassandraAsyncService asyncService;

    @Autowired
    private CassandraThreadedService threadedService;


    @Override
    public void onReceive(Object msg) {

        FeedEntity<String> feedEntity = (FeedEntity)msg;
        QueueService<String> queueService = feedEntity.getQueue();
        String system = feedEntity.getSystem();
        String date = feedEntity.getDate();
        String hour = feedEntity.getHour();
        if(!StringUtils.isEmpty(hour)){
            threadedService.feedDataByHour(system, date, Integer.parseInt(hour), queueService);
        }else {
            threadedService.feedDataByDates(system, date, queueService);
        }
    }

}
