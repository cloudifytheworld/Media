package com.huawei.mfg.bean;

import java.io.Serializable;

public class MfgSystemTableColumnMapping extends BaseBean implements Serializable {
    private int columnId;
    private String columnFamily;
    private String shortColumnName;
    private String forDb;
    private String columnDataType;

    public MfgSystemTableColumnMapping() {
    }

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    public String getShortColumnName() {
        return shortColumnName;
    }

    public void setShortColumnName(String shortColumnName) {
        this.shortColumnName = shortColumnName;
    }

    public String getForDb() {
        return forDb;
    }

    public void setForDb(String forDb) {
        this.forDb = forDb;
    }

    public String getColumnDataType() {
        return columnDataType;
    }

    public void setColumnDataType(String columnDataType) {
        this.columnDataType = columnDataType;
    }

    @Override
    public String toString() {
        return "MfgSystemTableColumnMapping{" +
                "columnId=" + columnId +
                ", columnFamily='" + columnFamily + '\'' +
                ", shortColumnName='" + shortColumnName + '\'' +
                ", forDb='" + forDb + '\'' +
                ", columnDataType='" + columnDataType + '\'' +
                ", " + super.toString() +
                '}';
    }
}
