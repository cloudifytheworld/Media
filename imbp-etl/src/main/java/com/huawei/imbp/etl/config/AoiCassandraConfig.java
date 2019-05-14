package com.huawei.imbp.etl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;

/**
 * @author Charles(Li) Cai
 * @date 5/13/2019
 */

@Configuration
@EnableReactiveCassandraRepositories
public class AoiCassandraConfig extends CassandraConfig {


    @Override
    public String getKeyspaceName() {
        return "images";
    }
}
