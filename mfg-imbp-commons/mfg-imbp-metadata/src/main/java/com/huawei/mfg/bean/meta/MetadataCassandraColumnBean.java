package com.huawei.mfg.bean.meta;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetadataCassandraColumnBean extends MetadataMappingBean {
    private String keyspace;
    private String columnFormat;
    private String columnPattern;
    private Boolean nullable;
    @JsonProperty("is_object")
    private boolean targetField;
    private int columnSize;

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public String getColumnFormat() {
        return columnFormat;
    }

    public String getColumnPattern() {
        return columnPattern;
    }

    public void setColumnFormat(String columnFormat) {
        this.columnFormat = columnFormat;
    }

    public void setColumnPattern(String columnPattern) {
        this.columnPattern = columnPattern;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public boolean isTargetField() {
        return targetField;
    }

    public void setTargetField(boolean targetField) {
        this.targetField = targetField;
    }

    @Override
    public String toString() {
        return "MetadataCassandraColumnBean{" +
                "keyspace='" + keyspace + '\'' +
                ", columnFormat='" + columnFormat + '\'' +
                ", columnPattern='" + columnPattern + '\'' +
                ", nullable=" + nullable +
                ", targetField=" + targetField +
                ", columnSize=" + columnSize +
                "} " + super.toString();
    }
}
