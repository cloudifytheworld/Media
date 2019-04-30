package com.huawei.mfg.conf;

import static com.huawei.mfg.util.CommonConstants.*;

/**
 * JDBC Datasource configuration @see MfgDatasourceConfiguration
 */
public class MfgJDBCDatasourceConfiguration extends MfgConfiguration {

    public MfgJDBCDatasourceConfiguration() {
        super();
    }

    public String getPoolName() {
        return this.getString(JDBC_POOL_NAME);
    }

    public void setPoolName(String poolName) {
        this.setStringProperty(JDBC_POOL_NAME, poolName);
    }

    public String getUsername() {
        return this.getString(JDBC_DB_USERNAME);
    }

    public void setUsername(String username)
    {
        this.setStringProperty(JDBC_DB_USERNAME, username);
    }

    public String getPassword() {
        return this.getString(JDBC_DB_PASSWORD);
    }

    public void setPassword(String password) {
        this.setStringProperty(JDBC_DB_PASSWORD, password);
    }

    public String getDatasourceClassname() {
        return this.getString(JDBC_DB_DATASOURCE_CLASS);
    }

    public void setDatasourceClassname(String datasourceClassname) {
        this.setStringProperty(JDBC_DB_DATASOURCE_CLASS, datasourceClassname);
    }

    public String getConnectionTestQuery() {
        return this.getString(JDBC_MYSQL_TEST_QUERY);
    }

    public void setConnectionTestQuery(String connectionTestQuery) {
        this.setStringProperty(JDBC_MYSQL_TEST_QUERY, connectionTestQuery);
    }

    public String getHost() {
        return this.getString(JDBC_DB_HOST);
    }

    public void setHost(String host) {
        this.setStringProperty(JDBC_DB_HOST, host);
    }

    public int getPort() {
        return this.getInteger(JDBC_DB_PORT);
    }

    public void setPort(int port) {
        this.setIntegerProperty(JDBC_DB_PORT, port);
    }

    public String getDatabaseName() {
        return this.getString(JDBC_DB_NAME);
    }

    public void setDatabaseName(String databaseName) {
        this.setStringProperty(JDBC_DB_NAME, databaseName);
    }

    public String getOtherProperties() {
        return this.getString(JDBC_DB_PROPERTIES);
    }

    public void setProperties(String properties) {
        this.setStringProperty(JDBC_DB_PROPERTIES, properties);
    }

    public String getOracleServiceName() {
        return this.getString(ORACLE_SERVICE_NAME);
    }

    public void setOracleServiceName(String serviceName) {
        this.setStringProperty(ORACLE_SERVICE_NAME, serviceName);
    }

    public String getOracleServiceId() {
        return this.getString(ORACLE_SID);
    }

    public void setOracleServiceId(String serviceId) {
        this.setStringProperty(ORACLE_SID, serviceId);
    }

    @Override
    public String toString() {
        return "MfgJDBCDatasourceConfiguration: " + super.toString();
    }
}
