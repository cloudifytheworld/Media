package com.huawei.mfg.bean;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class MfgSystemTableToHive extends BaseBean implements Serializable {
    private int tableId;

    @NotNull
    private String tableName;
    private String location;
    private boolean enabled;

    public MfgSystemTableToHive() {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "MfgSystemTableToHive{" +
                "tableId=" + tableId +
                ", tableName='" + tableName + '\'' +
                ", location='" + location + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
