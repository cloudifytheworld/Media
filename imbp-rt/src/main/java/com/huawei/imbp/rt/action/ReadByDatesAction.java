package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.huawei.imbp.rt.entity.DateDevice;
import com.huawei.imbp.rt.service.CassandraAsyncService;
import com.huawei.imbp.rt.util.StatisticManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * @author Charles(Li) Cai
 * @date 5/17/2019
 */

@Component("readByDatesAction")
@Log4j2
@Scope("prototype")
public class ReadByDatesAction extends UntypedAbstractActor {

    @Autowired
    CassandraAsyncService cassandraAsyncService;

    @Override
    public void onReceive(Object msg) {

        long start = System.currentTimeMillis();
        DateDevice dateDevice = (DateDevice)msg;
        cassandraAsyncService.getDates(dateDevice);
        long last = (System.currentTimeMillis() - start)/(1000);

        StatisticManager.putDay(dateDevice.getDate(), last);
        log.info(dateDevice.getDate()+" takes "+last+" # of cells "+StatisticManager.counter+" size(M) "+String.format("%.2f", StatisticManager.total));
        StatisticManager.init();
    }

}
