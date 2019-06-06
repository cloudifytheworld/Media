package com.huawei.imbp.etl.action;

import akka.actor.UntypedAbstractActor;
import com.google.common.base.Throwables;
import com.huawei.imbp.etl.entity.ActionEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * @author Charles(Li) Cai
 * @date 3/13/2019
 */

@Component("logAction")
@Log4j2
@Scope("prototype")
public class LogAction extends UntypedAbstractActor {

    @Override
    public void onReceive(Object msg) {

        if(Throwable.class.isInstance(msg)) {
            onException((Throwable)msg);
        }else if(String.class.isInstance(msg)){
            onErrorMsg((String)msg);
        }else if(ActionEntity.class.isInstance(msg)){
            //Todo push to distributed logging system
        }

    }

    private void onException(Throwable msg){

        String exception = Throwables.getStackTraceAsString(msg);
        log.error(exception);
    }

    private void onErrorMsg(String msg){

        log.error(msg);
    }
}
