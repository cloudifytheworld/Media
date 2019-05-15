package com.huawei.imbp.rt.config;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

/**
 * @author Charles(Li) Cai
 * @date 3/13/2019
 */

public class ImbpEtlActionProducer implements IndirectActorProducer {

    private final ApplicationContext applicationContext;
    private final String actionBeanName;

    public ImbpEtlActionProducer(ApplicationContext applicationContext, String actionBeanName) {
        this.applicationContext = applicationContext;
        this.actionBeanName = actionBeanName;
    }

    @Override
    public Actor produce() {
        return (Actor) applicationContext.getBean(actionBeanName);
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(actionBeanName);
    }
}

