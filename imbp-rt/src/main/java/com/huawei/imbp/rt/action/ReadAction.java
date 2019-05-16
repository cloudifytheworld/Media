package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.huawei.imbp.rt.entity.DateDevice;
import com.huawei.imbp.rt.service.CassandraRtService;
import com.huawei.imbp.rt.util.StatisticManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



/**
 * @author Charles(Li) Cai
 * @date 5/3/2019
 */

@Component("readAction")
@Log4j2
@Scope("prototype")
public class ReadAction extends UntypedAbstractActor {

    @Autowired
    CassandraRtService cassandraRtService;

    @Override
    public void onReceive(Object msg) {

        if(DateDevice.class.isInstance(msg)) {
            long start = System.currentTimeMillis();
            DateDevice dateDevice = (DateDevice)msg;
            int hour = dateDevice.getHour();
            cassandraRtService.getData(dateDevice);
            long last = (System.currentTimeMillis() - start)/(1000);
            StatisticManager.putDay(dateDevice.getDate(), last);
            StatisticManager.putDay(dateDevice.getDate()+"-"+hour, last);
            log.info(StatisticManager.eachDay.toString());
            log.info(dateDevice.getDate()+" takes "+last+" # of cells "+CassandraRtService.counter+" size(M) "+String.format("%.2f", StatisticManager.total));
//            Gson gson = new Gson();
//            String map = gson.toJson(StatisticManager.statistics);
//            String deviceStats = gson.toJson(StatisticManager.deviceSize);
//            String eachHourStats = gson.toJson(StatisticManager.eachHourSize);
//            String eachMinusStats = gson.toJson(StatisticManager.eachMinusSize);
            StatisticManager.init();
        }

    }

}