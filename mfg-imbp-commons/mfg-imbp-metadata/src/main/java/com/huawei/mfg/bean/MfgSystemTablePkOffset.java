package com.huawei.mfg.bean;

import java.io.Serializable;

public class MfgSystemTablePkOffset extends BaseBean implements Serializable {
    private int tableId;
    private String lastOffset;

    public MfgSystemTablePkOffset() {
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(String lastOffset) {
        this.lastOffset = lastOffset;
    }

    @Override
    public String toString() {
        return "MfgSystemTablePkOffset{" +
                "tableId=" + tableId +
                ", lastOffset='" + lastOffset + '\'' +
                '}';
    }

}
