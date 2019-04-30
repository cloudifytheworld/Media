package com.huawei.mfg.bean;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class MfgSystemTableColumn extends BaseBean implements Serializable {
    private int tableId;

    @NotNull
    private String columnName;
    private String columnDataType;
    private int columnSize;
    private boolean nullable;
    private String columnFormat;
    private String columnPattern;

    private String columnNameLC; //extra field
    private String typeName; //exra

    public MfgSystemTableColumn() {
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
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

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
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

    public String getColumnNameLC() {
        return columnNameLC;
    }

    public void setColumnNameLC(String columnNameLC) {
        this.columnNameLC = columnNameLC;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "MfgSystemTableColumn{" +
                "tableId=" + tableId +
                ", columnName='" + columnName + '\'' +
                ", columnDataType='" + columnDataType + '\'' +
                ", columnSize=" + columnSize +
                ", nullable=" + nullable +
                ", columnFormat='" + columnFormat + '\'' +
                ", columnPattern='" + columnPattern + '\'' +
                ", columnNameLC='" + columnNameLC + '\'' +
                ", typeName='" + typeName + '\'' +
                "} " + super.toString();
    }
}
