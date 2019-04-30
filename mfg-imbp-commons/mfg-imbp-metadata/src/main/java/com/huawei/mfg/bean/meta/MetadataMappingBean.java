package com.huawei.mfg.bean.meta;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class MetadataMappingBean implements Serializable {
    @JsonProperty("columnName")
    protected String mappedColumnName;
    protected String columnDataType;
    protected String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getMappedColumnName() {
        return mappedColumnName;
    }

    public void setMappedColumnName(String mappedColumnName) {
        this.mappedColumnName = mappedColumnName;
    }

    public String getColumnDataType() {
        return columnDataType;
    }

    public void setColumnDataType(String columnDataType) {
        this.columnDataType = columnDataType;
    }

    @Override
    public String toString() {
        return "MetadataMappingBean{" +
                "mappedColumnName='" + mappedColumnName + '\'' +
                ", columnDataType='" + columnDataType + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
