package com.huawei.mfg.bean;

import java.io.Serializable;

public class TableColumnsAndMapping implements Serializable {
    private String tableName;
    private String columnName;
    private String columnDataType;
    private String shortColumnName;
    private String columnFamily;
    private String mappedDataType;
    private String forDb;

    public TableColumnsAndMapping() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnDataType() {
        return columnDataType;
    }

    public void setColumnDataType(String columnDataType) {
        this.columnDataType = columnDataType;
    }

    public String getShortColumnName() {
        return shortColumnName;
    }

    public void setShortColumnName(String shortColumnName) {
        this.shortColumnName = shortColumnName;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    public String getMappedDataType() {
        return mappedDataType;
    }

    public void setMappedDataType(String mappedDataType) {
        this.mappedDataType = mappedDataType;
    }

    public String getForDb() {
        return forDb;
    }

    public void setForDb(String forDb) {
        this.forDb = forDb;
    }

    @Override
    public String toString() {
        return "TableColumnsAndMapping{" +
                "tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", columnDataType='" + columnDataType + '\'' +
                ", shortColumnName='" + shortColumnName + '\'' +
                ", columnFamily='" + columnFamily + '\'' +
                ", mappedDataType='" + mappedDataType + '\'' +
                ", forDb='" + forDb + '\'' +
                '}';
    }
}
