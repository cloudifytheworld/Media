package com.huawei.mfg.bean.meta;

public class MetadataHBaseColumnBean extends MetadataColumnBean {

    private String columnFamily;
    public String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }
}
