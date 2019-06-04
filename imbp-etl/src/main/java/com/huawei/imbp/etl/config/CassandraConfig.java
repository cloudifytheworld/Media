package com.huawei.imbp.etl.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.cql.session.DefaultBridgedReactiveSession;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 5/13/2019
 */

@RefreshScope
public abstract class CassandraConfig {//extends AbstractReactiveCassandraConfiguration {

    @Value("#{${db.cassandra}}")
    public Map<String, String> cassandraConfig;

//    @Override
//    protected String getContactPoints(){
//        return cassandraConfig.get("contact-points");
//    }
//
//    @Override
//    public String[] getEntityBasePackages(){
//      String[] entities = {"com.huawei.imbp.etl.entity.AoiEntity"};
//      return entities;
//    }

    @Bean
    @RefreshScope
    public ReactiveCassandraOperations aoiSession() {
//        PoolingOptions poolingOptions = new PoolingOptions();
//        poolingOptions.setHeartbeatIntervalSeconds(Integer.parseInt(cassandraConfig.get("heart-beat")));
//        poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 100)
//                .setMaxConnectionsPerHost(HostDistance.LOCAL, 300)
//                .setNewConnectionThreshold(HostDistance.LOCAL, 200)
//                .setConnectionsPerHost(HostDistance.LOCAL, 1, 250);

        Cluster cluster = Cluster.builder()
                .addContactPoints(cassandraConfig.get("contact-points").split(","))
//                .withPoolingOptions(poolingOptions)
                .withLoadBalancingPolicy(new RoundRobinPolicy())
                .withoutMetrics()
                .withoutJMXReporting()
                .build();

        Session session = cluster.connect("images");
        ReactiveCassandraOperations template = new ReactiveCassandraTemplate(new DefaultBridgedReactiveSession(session));
        return template;
    }

//    @Bean
//    public ReactiveCassandraOperations cassandraTemplate(){
//        ReactiveCassandraOperations template = new ReactiveCassandraTemplate(new DefaultBridgedReactiveSession(session()));
//        return template;
//
//    }

}
