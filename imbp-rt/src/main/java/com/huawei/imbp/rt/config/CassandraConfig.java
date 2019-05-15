package com.huawei.imbp.rt.config;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.QueryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 5/13/2019
 */

@RefreshScope
public abstract class CassandraConfig extends AbstractReactiveCassandraConfiguration {

    @Value("#{${db.cassandra}}")
    public Map<String, String> cassandraConfig;

    @Override
    protected String getContactPoints(){
        return cassandraConfig.get("contact-points");
    }

    @Override
    public String[] getEntityBasePackages(){
      String[] entities = {"com.huawei.imbp.etl.entity.AoiEntity"};
      return entities;
    }

    @Override
    public PoolingOptions getPoolingOptions(){

        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setHeartbeatIntervalSeconds(Integer.parseInt(cassandraConfig.get("heart-beat")));
        poolingOptions.setConnectionsPerHost(HostDistance.LOCAL, 1, 1300)
                .setMaxRequestsPerConnection(HostDistance.LOCAL, 1350)
                .setNewConnectionThreshold(HostDistance.LOCAL, 1200)
                .setCoreConnectionsPerHost(HostDistance.LOCAL, 1300);

        return poolingOptions;
    }


    @Override
    public QueryOptions getQueryOptions(){

        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setConsistencyLevel(ConsistencyLevel.ONE);
        return queryOptions;
    }
}
