package com.fw.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.google.common.base.Throwables;
import com.fw.imbp.rt.common.JobStatus;
import com.fw.imbp.rt.service.CassandraReactiveService;
import com.fw.imbp.rt.transfer.DataManager;
import com.fw.imbp.rt.transfer.DataServer;
import com.fw.imbp.rt.transfer.JobStorage;
import com.fw.imbp.rt.transfer.ServerActionData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    JobStorage jobStorage;

    @Autowired
    CassandraReactiveService cassandraReactiveService;

    @Value("${data.inMemoryWrite}")
    public boolean inMemoryWrite;


    @Value("${data.sleepLimit}")
    public int sleepLimit;

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

        DataServer dataServer = new DataServer(serverData.getSocketAddress(),
                serverData.getParticipatedClients(), serverData.getFilePath(), groupId, inMemoryWrite);


        try {
            log.info(String .format("----total participated server(s) %d for group %s",
                    serverData.getParticipatedClients(), groupId));
            dataServer.run(ready).start((endMsg) -> {
                String end[] = endMsg.split(":");
                jobStorage.setClientStatus(end[1], end[2], JobStatus.valueOf(end[3].trim()));
                jobs.countDown();
                log.info(String .format("-------------JOB finish #%d for group %s --------------",
                        jobs.getCount(), groupId));

            });

            ready.await();

            dataManager.call(groupId);

            jobs.await();
            Thread.sleep(sleepLimit);
            dataManager.clear(groupId);
            dataServer.close();
            log.info("server closed");
        }catch (Exception e){
             log.error(this.getClass()+"---"+
                     Throwables.getStackTraceAsString(e));
        }
    }

}
