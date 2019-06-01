package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.google.common.base.Throwables;
import com.huawei.imbp.rt.service.CassandraAsyncService;
import com.huawei.imbp.rt.transfer.ClientActionData;
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

@Component("clientAction")
@Log4j2
@Scope("prototype")
public class ClientAction extends UntypedAbstractActor {

    @Autowired
    CassandraAsyncService cassandraAsyncService;

    @Override
    public void onReceive(Object msg) {

        if(ClientActionData.class.isInstance(msg)) {
            ClientActionData client = (ClientActionData)msg;
            final CountDownLatch jobs = client.getJobs();
            final DataReceiver dataReceiver = client.getDataReceiver();
            final String groupId = dataReceiver.getGroupId();
            final DataManager dataManager = client.getDataManager();
            dataManager.execute(client.getSystem(), client.getStart(),
                    client.getEnd(), client.getServerIp(), groupId);

            log.info("writing data to file ["+groupId+"].......");
            try {
                jobs.await();
                dataManager.clear(groupId);
                dataReceiver.close();
                log.info("task completed for group "+groupId);
            }catch (Exception e){
                log.error(Throwables.getStackTraceAsString(e));
            }
        }

    }

}
