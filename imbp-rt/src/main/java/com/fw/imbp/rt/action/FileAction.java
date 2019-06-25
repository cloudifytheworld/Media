package com.fw.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.fw.imbp.rt.service.CassandraReactiveService;
import com.fw.imbp.rt.service.CassandraAsyncService;
import com.fw.imbp.rt.entity.ClientData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author Charles(Li) Cai
 * @date 6/13/2019
 */

@Component("fileAction")
@Log4j2
public class FileAction extends UntypedAbstractActor {

    @Autowired
    CassandraAsyncService asyncService;

    @Autowired
    CassandraReactiveService reactiveService;

    @Override
    public void onReceive(Object msg) {

        if (ClientData.class.isInstance(msg)) {
            asyncService.getDataByDate((ClientData) msg);
        }
    }
}
