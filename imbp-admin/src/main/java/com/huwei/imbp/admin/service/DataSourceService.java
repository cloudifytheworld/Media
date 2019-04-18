package com.huwei.imbp.admin.service;

import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@Slf4j
public class DataSourceService {


    @Value("${imbp.jdbc.db.username}")
    private String username;

    @Value("${imbp.jdbc.db.password}")
    private String password;

    @Value("${imbp.jdbc.db.datasourceClassName}")
    private String datasourceClassName;

    @Value("${imbp.jdbc.db.connectionTestQuery}")
    private String connectionTestQuery;

    @Value("${imbp.jdbc.db.host}")
    private String host;

    @Value("${imbp.jdbc.db.port}")
    private Integer port;

    @Value("${imbp.jdbc.db.dbName}")
    private String dbName;

    @Value("${imbp.jdbc.db.properties}")
    private String properties;

    @Value("${imbp.jdbc.db.poolName}")
    private String poolName;

    private MfgJDBCDatasourceConfiguration configure(){

        MfgJDBCDatasourceConfiguration config = new MfgJDBCDatasourceConfiguration();
        config.setPoolName(poolName);
        config.setUsername(username);
        config.setPassword(password);
        config.setDatasourceClassname(datasourceClassName);
        config.setConnectionTestQuery(connectionTestQuery);
        config.setHost(host);
        config.setPort(port);
        config.setDatabaseName(dbName);
        config.setProperties(properties);

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
