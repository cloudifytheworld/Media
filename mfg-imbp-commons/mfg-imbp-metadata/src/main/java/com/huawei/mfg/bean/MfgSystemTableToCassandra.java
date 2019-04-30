package com.huawei.mfg.bean;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class MfgSystemTableToCassandra extends BaseBean implements Serializable {
    private int tableId;

    @NotNull
    private String tableName;
    private String keySpace;
    private String partitionKeys;
    private String clusteringKeys;
    private String description;
    private boolean enabled;

    public MfgSystemTableToCassandra() {
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

    public String getKeySpace() {
        return keySpace;
    }

    public void setKeySpace(String keySpace) {
        this.keySpace = keySpace;
    }

    public String getPartitionKeys() {
        return partitionKeys;
    }

    public void setPartitionKeys(String partitionKeys) {
        this.partitionKeys = partitionKeys;
    }

    public String getClusteringKeys() {
        return clusteringKeys;
    }

    public void setClusteringKeys(String clusteringKeys) {
        this.clusteringKeys = clusteringKeys;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "MfgSystemTableToCassandra{" +
                "tableId=" + tableId +
                ", tableName='" + tableName + '\'' +
                ", keySpace='" + keySpace + '\'' +
                ", partitionKeys='" + partitionKeys + '\'' +
                ", clusteringKeys='" + clusteringKeys + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                "} " + super.toString();
    }

}
