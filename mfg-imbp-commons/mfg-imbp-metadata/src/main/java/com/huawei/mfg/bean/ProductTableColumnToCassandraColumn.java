package com.huawei.mfg.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Bean for table 'product_table_column_to_cassandra_column_mapping'
 */
public class ProductTableColumnToCassandraColumn extends BaseBean implements Serializable {
    private int ptableColumnToCassandraId; //junction table product_table_column_to_cassandra's id
    private String mappedColumnName;
    private int columnSize;
    private String columnFormat;
    private String columnPattern;
    private boolean nullable;
    @JsonProperty("targeted")
    private boolean targetField;
    private String description;

    @JsonProperty("colId")
    private int columnId;

    @JsonProperty("cassTableId")
    private int cassandraTableId;

    @JsonProperty("colCassRefId")
    private int columnCassandraRefId; //product_table_column_to_cassandra's id

    public ProductTableColumnToCassandraColumn() {
    }

    public int getPtableColumnToCassandraId() {
        return ptableColumnToCassandraId;
    }

    public void setPtableColumnToCassandraId(int ptableColumnToCassandraId) {
        this.ptableColumnToCassandraId = ptableColumnToCassandraId;
    }

    public String getMappedColumnName() {
        return mappedColumnName;
    }

    public void setMappedColumnName(String mappedColumnName) {
        this.mappedColumnName = mappedColumnName;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
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

    public boolean isTargetField() {
        return targetField;
    }

    public void setTargetField(boolean targetField) {
        this.targetField = targetField;
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
        return "ProductTableColumnToCassandraColumn{" +
                "ptableColumnToCassandraId=" + ptableColumnToCassandraId +
                ", mappedColumnName='" + mappedColumnName + '\'' +
                ", columnSize=" + columnSize +
                ", columnFormat='" + columnFormat + '\'' +
                ", columnPattern='" + columnPattern + '\'' +
                ", nullable=" + nullable +
                ", targetField=" + targetField +
                ", description='" + description + '\'' +
                ", columnId=" + columnId +
                ", cassandraTableId=" + cassandraTableId +
                ", columnCassandraRefId=" + columnCassandraRefId +
                "} " + super.toString();
    }

}
