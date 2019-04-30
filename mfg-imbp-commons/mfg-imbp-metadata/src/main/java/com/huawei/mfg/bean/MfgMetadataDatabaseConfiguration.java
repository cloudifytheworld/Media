package com.huawei.mfg.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MfgMetadataDatabaseConfiguration extends BaseBean implements Serializable {
    private String host;
    private int port;
    private String username;
    private String password;
    private String description;

    @JsonProperty("dbName")
    private String database;

    @JsonProperty("dbType")
    private String databaseType;
    private String connectionPoolName;
    private String testQuery;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getConnectionPoolName() {
        return connectionPoolName;
    }

    public void setConnectionPoolName(String connectionPoolName) {
        this.connectionPoolName = connectionPoolName;
    }

    public String getTestQuery() {
        return testQuery;
    }

    public void setTestQuery(String testQuery) {
        this.testQuery = testQuery;
    }

    @Override
    public String toString() {
        return "MfgMetadataDatabaseConfiguration{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", description='" + description + '\'' +
                ", database='" + database + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", connectionPoolName='" + connectionPoolName + '\'' +
                ", testQuery='" + testQuery + '\'' +
                "} " + super.toString();
    }

}
