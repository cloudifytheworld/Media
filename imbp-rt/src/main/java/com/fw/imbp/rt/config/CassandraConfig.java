package com.fw.imbp.rt.config;

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
 * @date 5/20/2019
 */

@Configuration
@RefreshScope
public class CassandraConfig {

    @Value("#{${db.cassandra}}")
    public Map<String, String> cassandraConfig;


    @Bean
    @RefreshScope
    public Session cassandraSession(){

        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setHeartbeatIntervalSeconds(Integer.parseInt(cassandraConfig.get("heart-beat")));
        poolingOptions.setMaxRequestsPerConnection(HostDistance.LOCAL, 350)
                .setNewConnectionThreshold(HostDistance.LOCAL, 200)
                .setConnectionsPerHost(HostDistance.LOCAL, 1, 300)
                .setCoreConnectionsPerHost(HostDistance.LOCAL, 300);

        SocketOptions options = new SocketOptions();
        options.setConnectTimeoutMillis(50000);
        options.setReadTimeoutMillis(50000);
        options.setTcpNoDelay(true);

        Cluster cluster = Cluster.builder()
                .addContactPoints(cassandraConfig.get("contact-points").split(","))
                .withProtocolVersion(ProtocolVersion.V4)
                .withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.ONE))
                .withPoolingOptions(poolingOptions)
                .withSocketOptions(options)
                .withLoadBalancingPolicy(new RoundRobinPolicy())
                .withoutMetrics()
                .withoutJMXReporting()
                .build();

        return cluster.connect();
    }

    @Bean
    public ReactiveCassandraOperations cassandraDataSession(){
        ReactiveCassandraOperations template = new ReactiveCassandraTemplate(new DefaultBridgedReactiveSession(cassandraSession()));
        return template;

    }


}
