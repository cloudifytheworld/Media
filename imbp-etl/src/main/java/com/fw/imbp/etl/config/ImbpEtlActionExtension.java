package com.fw.imbp.etl.config;

import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Charles(Li) Cai
 * @date 3/13/2019
 */

@Component
public class ImbpEtlActionExtension implements Extension {

    private ApplicationContext applicationContext;


    public void initialize(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Props props(String actionBeanName) {
        return Props.create(ImbpEtlActionProducer.class,
                applicationContext, actionBeanName);
    }
}
