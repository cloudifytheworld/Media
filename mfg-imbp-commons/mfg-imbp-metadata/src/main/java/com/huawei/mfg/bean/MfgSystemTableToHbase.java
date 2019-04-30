package com.huawei.mfg.bean;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class MfgSystemTableToHbase extends BaseBean implements Serializable {
    private int tableId;

    @NotNull
    private String tableName;
    private String columnFamilies;
    private String rowKeyColumns;
    private boolean enabled;

    public MfgSystemTableToHbase() {
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

    public String getColumnFamilies() {
        return columnFamilies;
    }

    public void setColumnFamilies(String columnFamilies) {
        this.columnFamilies = columnFamilies;
    }

    public String getRowKeyColumns() {
        return rowKeyColumns;
    }

    public void setRowKeyColumns(String rowKeyColumns) {
        this.rowKeyColumns = rowKeyColumns;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "MfgSystemTableToHbase{" +
                "tableId=" + tableId +
                ", tableName='" + tableName + '\'' +
                ", columnFamilies='" + columnFamilies + '\'' +
                ", rowKeyColumns='" + rowKeyColumns + '\'' +
                ", enabled=" + enabled +
                '}';
    }

}
