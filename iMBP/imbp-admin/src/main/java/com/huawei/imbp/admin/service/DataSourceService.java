package com.huawei.imbp.admin.service;

import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 2/28/2019
 */

@Component
@RefreshScope
@Slf4j
public class DataSourceService {


    @Value("#{${db.mysql}}")
    private Map<String, String> dbConfig;

    private MfgJDBCDatasourceConfiguration configure(){

        MfgJDBCDatasourceConfiguration config = new MfgJDBCDatasourceConfiguration();
        config.setPoolName(dbConfig.get("poolName"));
        config.setUsername(dbConfig.get("username"));
        config.setPassword(dbConfig.get("password"));
        config.setDatasourceClassname(dbConfig.get("datasourceClassName"));
        config.setConnectionTestQuery(dbConfig.get("connectionTestQuery"));
        config.setHost(dbConfig.get("host"));
        config.setPort(Integer.parseInt(dbConfig.get("port")));
        config.setDatabaseName(dbConfig.get("dbName"));
        config.setProperties(dbConfig.get("properties"));

        return config;
    }

    public SQLDatasource dataSource(){

        SQLDatasource ds = new SQLDatasource(configure());

        try {
            ds.connect();
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }

        return ds;
    }
}
