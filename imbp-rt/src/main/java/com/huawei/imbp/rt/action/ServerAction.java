package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.google.common.base.Throwables;
import com.huawei.imbp.rt.service.CassandraAsyncService;
import com.huawei.imbp.rt.transfer.DataManager;
import com.huawei.imbp.rt.transfer.DataReceiver;
import com.huawei.imbp.rt.transfer.ServerActionData;
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

        if(ServerActionData.class.isInstance(msg)) {
            onProcess((ServerActionData)msg);
        }

    }

    public void onProcess(ServerActionData serverData){

        CountDownLatch jobs = serverData.getJobs();
        CountDownLatch ready = serverData.getReady();
        DataManager dataManager = serverData.getDataManager();
        String groupId = dataManager.getGroupId();

        DataReceiver dataReceiver = new DataReceiver(serverData.getSocketAddress(),
                serverData.getParticipatedClients(), serverData.getFilePath(), groupId);


        try {
            dataReceiver.run(ready).start(() -> {
                jobs.countDown();
            });

            ready.await();

            dataManager.call(groupId);

            jobs.await();
            log.info("starting to close server");
            dataManager.clear(groupId);
            dataReceiver.close();

        }catch (Exception e){
             log.error(this.getClass()+" "+
                     Throwables.getStackTraceAsString(e));
        }
    }

}
