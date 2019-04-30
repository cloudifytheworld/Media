package com.huawei.mfg.bean.meta;

public class MetadataTableCassandraBean {

    public String keyspace;
    public String tableName;
    public String primaryKeys;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(String primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    @Override
    public String toString() {
        return "MetadataTableCassandraBean{" +
                "keyspace='" + keyspace + '\'' +
                ", tableName='" + tableName + '\'' +
                ", primaryKeys='" + primaryKeys + '\'' +
                '}';
    }
}
