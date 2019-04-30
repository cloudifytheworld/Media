package com.huawei.mfg.pool;

import com.google.common.base.Strings;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLDatasource {
    private MfgJDBCDatasourceConfiguration config;
    private HikariConfig hikariConfig;
    private HikariDataSource hikariDataSource;

    public SQLDatasource(MfgJDBCDatasourceConfiguration config) {
        this.config = config;
        this.hikariConfig = new HikariConfig();
    }

    private void configure() {
//        hikariConfig.setJdbcUrl("jdbc:mysql://10.208.51.172:3306/mfg_metadata");
//        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
//        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
//        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        hikariConfig.setPoolName(this.config.getPoolName());
        hikariConfig.setUsername(this.config.getUsername());
        hikariConfig.setPassword(this.config.getPassword());
        hikariConfig.setDataSourceClassName(this.config.getDatasourceClassname());

        if (!Strings.isNullOrEmpty(this.config.getConnectionTestQuery())) {
            hikariConfig.setConnectionTestQuery(this.config.getConnectionTestQuery());
        }

        hikariConfig.addDataSourceProperty("serverName", this.config.getHost());

        if (this.config.getDatasourceClassname().contains("OracleDataSource")) {
            hikariConfig.addDataSourceProperty("portNumber", this.config.getPort());
            hikariConfig.addDataSourceProperty("driverType", "thin");

            if (!Strings.isNullOrEmpty(this.config.getOracleServiceName())) {
                hikariConfig.addDataSourceProperty("serviceName", this.config.getOracleServiceName());
            }
        }
        else {
            hikariConfig.addDataSourceProperty("port", this.config.getPort());

            if (!Strings.isNullOrEmpty(this.config.getDatabaseName())) {
                hikariConfig.addDataSourceProperty("databaseName", this.config.getDatabaseName());
            }
        }

        if (!Strings.isNullOrEmpty(this.config.getOtherProperties())) {
            hikariConfig.addDataSourceProperty("properties", this.config.getOtherProperties());
        }

        hikariConfig.setConnectionTimeout(60000);
    }

    public void connect() throws SQLException {
        this.configure();
        this.hikariDataSource = new HikariDataSource(this.hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return this.hikariDataSource.getConnection();
    }

    public void close()throws SQLException {
        this.hikariDataSource.close();
    }

}
