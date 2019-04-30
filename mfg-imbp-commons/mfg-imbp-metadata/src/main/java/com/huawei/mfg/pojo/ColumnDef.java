package com.huawei.mfg.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColumnDef {
    private int id;
    private String columnName;
    private String columnNameLC;
    private String dataType;
    private String typeName;
    private int columnSize;
    private int bufferLen;
    private boolean nullable;
    private boolean isAutoincrement;
    private String columnDefault;

    @JsonCreator
    private ColumnDef(@JsonProperty("columnName") String columnName,
                      @JsonProperty("dataType") String dataType,
                      @JsonProperty("typeName") String typeName,
                      @JsonProperty("columnSize") int columnSize,
                      @JsonProperty("bufferLen") int bufferLen,
                      @JsonProperty("nullable") boolean nullable,
                      @JsonProperty("autoincrement") boolean isAutoincrement,
                      @JsonProperty("columnDefault") String columnDefault)
    {
        this.id = -1;
        this.columnName = columnName;
        this.columnNameLC = (this.columnName != null && !this.columnName.isEmpty()) ? this.columnName.toLowerCase() : null;
        this.dataType = dataType;
        this.typeName = typeName;
        this.columnSize = columnSize;
        this.bufferLen = bufferLen;
        this.nullable = nullable;
        this.isAutoincrement = isAutoincrement;
        this.columnDefault = columnDefault;
    }

    public static ColumnDef create(String columnName, String dataType, String typeName, int columnSize, int bufferLen,
                                   boolean nullable, boolean isAutoincrement, String columnDefault)
    {
        return new ColumnDef(columnName, dataType, typeName, columnSize, bufferLen, nullable, isAutoincrement, columnDefault);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnNameLC() {
        return columnNameLC;
    }

    public String getDataType() {
        return dataType;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getBufferLen() {
        return bufferLen;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isAutoincrement() {
        return isAutoincrement;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    @Override
    public String toString() {
        return "ColumnDef{" +
                "id=" + id +
                ", columnName='" + columnName + '\'' +
                ", columnNameLC='" + columnNameLC + '\'' +
                ", dataType='" + dataType + '\'' +
                ", typeName='" + typeName + '\'' +
                ", columnSize=" + columnSize +
                ", bufferLen=" + bufferLen +
                ", nullable=" + nullable +
                ", isAutoincrement=" + isAutoincrement +
                '}';
    }
}
