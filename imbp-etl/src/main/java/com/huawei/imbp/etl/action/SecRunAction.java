package com.huawei.imbp.etl.action;

import akka.actor.UntypedAbstractActor;
import com.huawei.imbp.etl.service.CassandraService;
import com.huawei.imbp.etl.service.LoggingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 4/18/2019
 */

@Component("secRunAction")
@Log4j2
@Scope("prototype")
public class SecRunAction extends UntypedAbstractActor {

    @Autowired
    CassandraService service;

    @Autowired
    LoggingService loggingService;

    @Override
    public void onReceive(Object message){

        if(List.class.isInstance(message)){
            List<Map<String, Object>> data = List.class.cast(message);
            data.stream().forEach(d ->{
                try{
                    service.onAoiProcess(d);
                }catch (Exception e){
                    log.error(e.getMessage());
                    loggingService.onFailure(e, d);
                }

            });
        }
    }
}
