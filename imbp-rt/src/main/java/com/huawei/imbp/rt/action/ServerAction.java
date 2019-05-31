package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.huawei.imbp.rt.entity.DateDevice;
import com.huawei.imbp.rt.service.CassandraAsyncService;
import com.huawei.imbp.rt.transfer.DataReceiver;
import com.huawei.imbp.rt.transfer.ServerData;
import com.huawei.imbp.rt.util.StatisticManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;


/**
 * @author Charles(Li) Cai
 * @date 5/3/2019
 */

@Component("serverAction")
@Log4j2
@Scope("prototype")
public class ServerAction extends UntypedAbstractActor {

    @Autowired
    CassandraAsyncService cassandraAsyncService;

    @Override
    public void onReceive(Object msg) {

        if(ServerData.class.isInstance(msg)) {
            ServerData server = (ServerData)msg;
            CountDownLatch jobs = server.getJobs();
            CountDownLatch ready = server.getReady();
            DataReceiver dataReceiver = server.getDataReceiver();
            dataReceiver.run(jobs);//, ready);
        }

    }

}
