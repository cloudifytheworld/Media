package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.huawei.imbp.rt.entity.ClientData;
import com.huawei.imbp.rt.service.CassandraAsyncService;
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
    private CassandraAsyncService asyncService;

    @Override
    public void onReceive(Object msg) {

        ClientData<String> clientData = (ClientData) msg;
        asyncService.feedDataByDate(clientData);

    }

}
