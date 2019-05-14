package com.huawei.imbp.etl.config;

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

}
