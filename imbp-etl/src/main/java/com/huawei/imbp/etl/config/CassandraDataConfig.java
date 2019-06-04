package com.huawei.imbp.etl.config;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.cql.session.DefaultBridgedReactiveSession;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 5/13/2019
 */

@Configuration
@RefreshScope
public class CassandraConfig {

    @Value("#{${db.cassandra}}")
    public Map<String, String> cassandraConfig;


    @Bean
    @RefreshScope
    public ReactiveCassandraOperations cassandraDataTemplate(){

        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setHeartbeatIntervalSeconds(Integer.parseInt(cassandraConfig.get("heart-beat")));
        poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 100)
                .setMaxConnectionsPerHost(HostDistance.LOCAL, 300)
                .setNewConnectionThreshold(HostDistance.LOCAL, 200)
                .setConnectionsPerHost(HostDistance.LOCAL, 1, 250);

        SocketOptions options = new SocketOptions();
        options.setConnectTimeoutMillis(50000);
        options.setReadTimeoutMillis(50000);
        options.setTcpNoDelay(true);

        Cluster cluster = Cluster.builder()
                .addContactPoints(cassandraConfig.get("contact-points").split(","))
                .withPoolingOptions(poolingOptions)
                .withLoadBalancingPolicy(new RoundRobinPolicy())
                .withSocketOptions(options)
                .withoutMetrics()
                .withoutJMXReporting()
                .build();

        Session session = cluster.connect("images");
        ReactiveCassandraOperations template = new ReactiveCassandraTemplate(new DefaultBridgedReactiveSession(session));
        return template;
    }

}
