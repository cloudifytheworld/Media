package com.huawei.imbp.rt;

import akka.actor.ActorSystem;
import ch.qos.logback.classic.Level;
import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.huawei.imbp.rt.config.ImbpEtlActionExtension;
import com.huawei.imbp.rt.util.Logging;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 4/8/2019
 */

@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
@Configuration
public class ImbpRtApplication {

    @Value("${logging.level-root}")
    public Level logLevel;

    @Bean
    @RefreshScope
    public Logging log(){
        Logging logging = new Logging();
        logging.setLogLevel(logLevel);
        return logging;
    }

    public static void main(String[] args) {
        SpringApplication.run(ImbpRtApplication.class, args);
    }

}
