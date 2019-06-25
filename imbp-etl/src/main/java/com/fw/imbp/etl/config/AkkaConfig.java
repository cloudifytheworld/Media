package com.fw.imbp.etl.config;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Charles(Li) Cai
 * @date 5/14/2019
 */

@Configuration
public class AkkaConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ImbpEtlActionExtension imbpEtlActionExtension;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem actorSystem = ActorSystem.create("ImbpEtlActionSystem", ConfigFactory.load());
        imbpEtlActionExtension.initialize(applicationContext);
        return actorSystem;
    }
}
