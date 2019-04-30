package com.huawei.mfg.bean;

import java.io.Serializable;

public class TableToHbaseColumnsMapping implements Serializable {
    private String tableName;
    private String hbaseTableName;
    private String columnDataType;
    private String mappedColumnName;
    private String columnFamily;
    private String columnName;


    public TableToHbaseColumnsMapping() {
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

    public String getHbaseTableName() {
        return hbaseTableName;
    }

    public void setHbaseTableName(String hbaseTableName) {
        this.hbaseTableName = hbaseTableName;
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

    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    @Override
    public String toString() {
        return "TableToHbaseColumnsMapping{" +
                "tableName='" + tableName + '\'' +
                ", hbaseTableName='" + hbaseTableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", columnDataType='" + columnDataType + '\'' +
                ", mappedColumnName='" + mappedColumnName + '\'' +
                ", columnFamily='" + columnFamily + '\'' +
                '}';
    }
}
