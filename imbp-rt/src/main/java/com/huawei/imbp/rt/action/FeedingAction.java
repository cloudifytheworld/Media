package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.huawei.imbp.rt.entity.FeedingEntity;
import com.huawei.imbp.rt.entity.RowsKey;
import com.huawei.imbp.rt.service.CassandraService;
import com.huawei.imbp.rt.service.QueueService;
import com.huawei.imbp.rt.util.WriteToFile;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author Charles(Li) Cai
 * @date 3/13/2019
 */

@Component("feedingAction")
@Log4j2
@Scope("prototype")
public class FeedingAction extends UntypedAbstractActor {

    @Autowired
    private CassandraService service;

    @Override
    public void onReceive(Object msg) {

        FeedingEntity<String> feedingEntity = (FeedingEntity)msg;
        QueueService<String> queueService = feedingEntity.getQueue();
        String system = feedingEntity.getSystem();
        String date = feedingEntity.getDate();

        service.getDataByFeeding(system, date, queueService);
    }

}
