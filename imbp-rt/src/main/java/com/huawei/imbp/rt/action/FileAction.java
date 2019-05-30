package com.huawei.imbp.rt.action;

import akka.actor.UntypedAbstractActor;
import com.datastax.driver.core.Row;
import com.huawei.imbp.rt.entity.RowsKey;
import com.huawei.imbp.rt.service.CassandraThreadedService;
import com.huawei.imbp.rt.transfer.ClientData;
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

@Component("fileAction")
@Log4j2
@Scope("prototype")
public class FileAction extends UntypedAbstractActor {

    @Autowired
    CassandraThreadedService service;

    @Override
    public void onReceive(Object msg) {

        if(List.class.isInstance(msg)) {
            //WriteToFile.writeToFile((List<Row>)msg);
        }

        if(RowsKey.class.isInstance(msg)){
            RowsKey rowsKey = (RowsKey)msg;
            WriteToFile.writeToFile(rowsKey.getRows(), rowsKey.getKey(), rowsKey.getHour(), rowsKey.getWhich());
        }

        if(ClientData.class.isInstance(msg)){
            service.getDataByDate((ClientData)msg);
        }
    }

}
