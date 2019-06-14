package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.huawei.imbp.rt.service.CassandraReactiveService;
import com.huawei.imbp.rt.service.CassandraAsyncService;
import com.huawei.imbp.rt.entity.ClientData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author Charles(Li) Cai
 * @date 3/13/2019
 */

@Component("fileAction")
@Log4j2
@Scope("prototype")
public class FileAction extends UntypedAbstractActor {

    @Autowired
    CassandraAsyncService threadedService;

    @Autowired
    CassandraReactiveService asyncService;

    @Value("${data.useAsync}")
    private boolean useAsync;

    @Override
    public void onReceive(Object msg) {

        if(List.class.isInstance(msg)) {
            //WriteToFile.writeToFile((List<Row>)msg);
        }

        if(ClientData.class.isInstance(msg)){

            if(useAsync){
                asyncService.getDataByDate((ClientData)msg);
            }else {
                threadedService.getDataByDate((ClientData) msg);
            }
        }
    }

}
