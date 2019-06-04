package com.huawei.imbp.etl;

import ch.qos.logback.classic.Level;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.etl.util.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 2/15/2019
 */

@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
@Configuration
public class ImbpEtlApplication {

    @Value("${logging.level-root}")
    public Level logLevel;

    @Bean
    @RefreshScope
    public Logging log(){
        Logging logging = new Logging();
        logging.setLogLevel(logLevel);
        return logging;
    }

//    @Bean
//    @RefreshScope
//    public ListenableFuture<Session> session(){
//
//        PoolingOptions poolingOptions = new PoolingOptions();
//        poolingOptions.setHeartbeatIntervalSeconds(Integer.parseInt(cassandraConfig.get("heart-beat")));
//        poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 100)
//                .setMaxConnectionsPerHost(HostDistance.LOCAL, 300)
//                .setNewConnectionThreshold(HostDistance.LOCAL, 200)
//                .setConnectionsPerHost(HostDistance.LOCAL, 1, 250);
//
//        Cluster cluster = Cluster.builder()
//                .addContactPoints(cassandraConfig.get("contact-points").split(","))
//                .withPoolingOptions(poolingOptions)
//                .withLoadBalancingPolicy(new RoundRobinPolicy())
//                .withoutMetrics()
//                .withoutJMXReporting()
//                .build();
//
//        return cluster.connectAsync();
//    }

    public static void main(String[] args) {
        SpringApplication.run(ImbpEtlApplication.class, args);
    }

}

