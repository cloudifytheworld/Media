package com.huawei.mfg.bean;

import java.io.Serializable;

public class MfgSystemTableMapping extends BaseBean implements Serializable {
    private int tableId;
    private String tableName;
    private String forDb;

    public MfgSystemTableMapping() {
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getForDb() {
        return forDb;
    }

    public void setForDb(String forDb) {
        this.forDb = forDb;
    }

    @Override
    public String toString() {
        return "MfgSystemTableMapping{" +
                "tableId=" + tableId +
                ", tableName='" + tableName + '\'' +
                ", forDb='" + forDb + '\'' +
                ", " + super.toString() +
                '}';
    }
}
