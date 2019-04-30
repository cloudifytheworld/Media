package com.huawei.mfg.bean.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataColumnBean implements Serializable {
    private String columnName;
    private String columnDataType;
    private Boolean nullable;
    private String columnFormat;
    private String columnPattern;

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

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
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

    @Override
    public String toString() {
        return "MetadataColumnBean{" +
                "columnName='" + columnName + '\'' +
                ", columnDataType='" + columnDataType + '\'' +
                ", nullable=" + nullable +
                ", columnFormat='" + columnFormat + '\'' +
                ", columnPattern='" + columnPattern + '\'' +
                '}';
    }
}
