package com.huawei.imbp.imbprt.Action;

import akka.actor.UntypedAbstractActor;
import com.datastax.driver.core.Row;
import com.huawei.imbp.imbprt.util.WriteToFile;
import lombok.extern.log4j.Log4j2;
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

    @Override
    public void onReceive(Object msg) {

        if(List.class.isInstance(msg)) {
            WriteToFile.writeToFile((List<Row>)msg);
        }
    }

}
