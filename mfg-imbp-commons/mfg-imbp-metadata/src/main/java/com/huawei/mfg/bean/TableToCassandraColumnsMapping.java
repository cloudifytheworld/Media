package com.huawei.mfg.bean;

import java.io.Serializable;

public class TableToCassandraColumnsMapping implements Serializable {
    private int columnId;
    private int cassandraColumnId;
    private int cassandraTableId;
    private int columnCassandraRefId; //product_table_column_to_cassandra's id
    private String tableName;
    private String cassandraTableName;
    private String columnDataType;
    private String mappedColumnName;
    private String description;
    private String keyspace;
    private String partitionKeys;
    private String clusteringKeys;
    private String columnName;
    private String columnFormat;
    private String columnPattern;
    private boolean nullable;
    private boolean isObject;
    private int columnSize;

    public TableToCassandraColumnsMapping() {
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public int getCassandraTableId() {
        return cassandraTableId;
    }

    public void setCassandraTableId(int cassandraTableId) {
        this.cassandraTableId = cassandraTableId;
    }

    public int getCassandraColumnId() {
        return cassandraColumnId;
    }

    public void setCassandraColumnId(int cassandraColumnId) {
        this.cassandraColumnId = cassandraColumnId;
    }

    public int getColumnCassandraRefId() {
        return columnCassandraRefId;
    }

    public void setColumnCassandraRefId(int columnCassandraRefId) {
        this.columnCassandraRefId = columnCassandraRefId;
    }

    public String getCassandraTableName() {
        return cassandraTableName;
    }

    public void setCassandraTableName(String cassandraTableName) {
        this.cassandraTableName = cassandraTableName;
    }

    public String getColumnDataType() {
        return columnDataType;
    }

    public void setColumnDataType(String columnDataType) {
        this.columnDataType = columnDataType;
    }

    public String getMappedColumnName() {
        return mappedColumnName;
    }

    public void setMappedColumnName(String mappedColumnName) {
        this.mappedColumnName = mappedColumnName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public String getPartitionKeys() {
        return partitionKeys;
    }

    public void setPartitionKeys(String partitionKeys) {
        this.partitionKeys = partitionKeys;
    }

    public String getClusteringKeys() {
        return clusteringKeys;
    }

    public void setClusteringKeys(String clusteringKeys) {
        this.clusteringKeys = clusteringKeys;
    }

    public String getColumnFormat() {
        return columnFormat;
    }

    public void setColumnFormat(String columnFormat) {
        this.columnFormat = columnFormat;
    }

    public String getColumnPattern() {
        return columnPattern;
    }

    public void setColumnPattern(String columnPattern) {
        this.columnPattern = columnPattern;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isObject() {
        return isObject;
    }

    public void setObject(boolean object) {
        isObject = object;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    @Override
    public String toString() {
        return "TableToCassandraColumnsMapping{" +
                "columnId=" + columnId +
                ", cassandraColumnId=" + cassandraColumnId +
                ", cassandraTableId=" + cassandraTableId +
                ", columnCassandraRefId=" + columnCassandraRefId +
                ", tableName='" + tableName + '\'' +
                ", cassandraTableName='" + cassandraTableName + '\'' +
                ", columnDataType='" + columnDataType + '\'' +
                ", mappedColumnName='" + mappedColumnName + '\'' +
                ", description='" + description + '\'' +
                ", keyspace='" + keyspace + '\'' +
                ", partitionKeys='" + partitionKeys + '\'' +
                ", clusteringKeys='" + clusteringKeys + '\'' +
                ", columnName='" + columnName + '\'' +
                ", columnFormat='" + columnFormat + '\'' +
                ", columnPattern='" + columnPattern + '\'' +
                ", nullable=" + nullable +
                ", isObject=" + isObject +
                ", columnSize=" + columnSize +
                '}';
    }
}
