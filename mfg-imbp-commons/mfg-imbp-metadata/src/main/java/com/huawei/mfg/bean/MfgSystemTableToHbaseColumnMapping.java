package com.huawei.mfg.bean;

import java.io.Serializable;

public class MfgSystemTableToHbaseColumnMapping extends BaseBean implements Serializable {
    private int columnId;
    private String columnFamily;
    private String mappedColumnName;

    public MfgSystemTableToHbaseColumnMapping() {
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

    public String getMappedColumnName() {
        return mappedColumnName;
    }

    public void setMappedColumnName(String mappedColumnName) {
        this.mappedColumnName = mappedColumnName;
    }

    @Override
    public String toString() {
        return "MfgSystemTableToHbaseColumnMapping{" +
                "columnId=" + columnId +
                ", columnFamily='" + columnFamily + '\'' +
                ", mappedColumnName='" + mappedColumnName + '\'' +
                "} " + super.toString();
    }
}
