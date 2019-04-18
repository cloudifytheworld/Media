package com.huawei.imbp.imbprt;

import akka.actor.ActorSystem;
import ch.qos.logback.classic.Level;
import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.imbprt.config.ImbpEtlActionExtension;
import com.huawei.imbp.imbprt.util.Logging;
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


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ImbpEtlActionExtension imbpEtlActionExtension;

    @Value("#{${db.cassandra}}")
    public Map<String, String> cassandraConfig;

    @Value("${logging.level-root}")
    public Level logLevel;

    @Bean
    @RefreshScope
    public Logging log(){
        Logging logging = new Logging();
        logging.setLogLevel(logLevel);
        return logging;
    }

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem actorSystem = ActorSystem.create("ImbpRtActionSystem", ConfigFactory.load());
        imbpEtlActionExtension.initialize(applicationContext);
        return actorSystem;
    }
    @Bean
    @RefreshScope
    public Session session(){

        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setHeartbeatIntervalSeconds(Integer.parseInt(cassandraConfig.get("heart-beat")));
        poolingOptions.setConnectionsPerHost(HostDistance.LOCAL, 1, 300)
                .setMaxRequestsPerConnection(HostDistance.LOCAL, 350)
                .setNewConnectionThreshold(HostDistance.LOCAL, 200)
                .setCoreConnectionsPerHost(HostDistance.LOCAL, 300);

        SocketOptions options = new SocketOptions();
        options.setConnectTimeoutMillis(50000);
        options.setReadTimeoutMillis(500000);
        options.setTcpNoDelay(true);

        Cluster cluster = Cluster.builder()
                .addContactPoints(cassandraConfig.get("contact-points").split(","))
                .withPoolingOptions(poolingOptions)
                .withSocketOptions(options)
                .withLoadBalancingPolicy(new RoundRobinPolicy())
                .withoutMetrics()
                .withoutJMXReporting()
                .build();

        return cluster.connect();
    }


    public static void main(String[] args) {
        SpringApplication.run(ImbpRtApplication.class, args);
    }

}
