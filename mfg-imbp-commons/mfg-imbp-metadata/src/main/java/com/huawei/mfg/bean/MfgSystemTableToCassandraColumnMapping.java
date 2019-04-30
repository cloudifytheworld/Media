package com.huawei.mfg.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@Deprecated
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfgSystemTableToCassandraColumnMapping extends BaseBean implements Serializable {
    @JsonProperty("colId")
    private int columnId;

    @JsonProperty("name")
    private String mappedColumnName;

    @JsonProperty("cassTableId")
    private int cassandraTableId;

    @JsonProperty("colCassRefId")
    private int columnCassandraRefId; //product_table_column_to_cassandra's id

    @JsonProperty("description")
    private String description;

    public MfgSystemTableToCassandraColumnMapping() {
    }

    public int getCassandraTableId() {
        return cassandraTableId;
    }

    public void setCassandraTableId(int cassandraTableId) {
        this.cassandraTableId = cassandraTableId;
    }

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public String getMappedColumnName() {
        return mappedColumnName;
    }

    public void setMappedColumnName(String mappedColumnName) {
        this.mappedColumnName = mappedColumnName;
    }

    public int getColumnCassandraRefId() {
        return columnCassandraRefId;
    }

    public void setColumnCassandraRefId(int columnCassandraRefId) {
        this.columnCassandraRefId = columnCassandraRefId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "MfgSystemTableToCassandraColumnMapping{" +
                "columnId=" + columnId +
                ", mappedColumnName='" + mappedColumnName + '\'' +
                ", cassandraTableId=" + cassandraTableId +
                ", columnCassandraRefId=" + columnCassandraRefId +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }
}
