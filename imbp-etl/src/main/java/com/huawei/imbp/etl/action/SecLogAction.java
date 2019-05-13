package com.huawei.imbp.etl.action;

import akka.actor.UntypedAbstractActor;
import com.huawei.imbp.etl.entity.ActionEntity;
import com.huawei.imbp.etl.service.SecLogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * @author Charles(Li) Cai
 * @date 3/13/2019
 */

@Component("secLogAction")
@Log4j2
@Scope("prototype")
public class SecLogAction extends UntypedAbstractActor {

    @Autowired
    SecLogService secLogService;

    @Override
    public void onReceive(Object message){

        secLogService.saveRawData((ActionEntity)message);
    }
}
