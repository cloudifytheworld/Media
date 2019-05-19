package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.huawei.imbp.rt.entity.FeedEntity;
import com.huawei.imbp.rt.service.CassandraService;
import com.huawei.imbp.rt.service.QueueService;
import lombok.extern.log4j.Log4j2;
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
    private CassandraService service;

    @Override
    public void onReceive(Object msg) {

        FeedEntity<String> feedEntity = (FeedEntity)msg;
        QueueService<String> queueService = feedEntity.getQueue();
        String system = feedEntity.getSystem();
        String date = feedEntity.getDate();

        service.getDataByFeeding(system, date, queueService);
    }

}
