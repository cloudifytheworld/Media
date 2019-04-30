package com.huawei.mfg.bean;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class MfgSystemTable extends BaseBean implements Serializable {
    private int systemId;

    @NotEmpty
    private String tableName;
    private String sampleTableName;
    private String partitionKeys;
    private String primaryKeys;
    private String uniqueKeys;

    @NotEmpty
    private String source; //redis, oracle or whatever
    private boolean enabled;

    public MfgSystemTable() {
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSampleTableName() {
        return sampleTableName;
    }

    public void setSampleTableName(String sampleTableName) {
        this.sampleTableName = sampleTableName;
    }

    public String getPartitionKeys() {
        return partitionKeys;
    }

    public void setPartitionKeys(String partitionKeys) {
        this.partitionKeys = partitionKeys;
    }

    public String getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(String primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public String getUniqueKeys() {
        return uniqueKeys;
    }

    public void setUniqueKeys(String uniqueKeys) {
        this.uniqueKeys = uniqueKeys;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "MfgSystemTable{" +
                "systemId=" + systemId +
                ", tableName='" + tableName + '\'' +
                ", sampleTableName='" + sampleTableName + '\'' +
                ", partitionKeys='" + partitionKeys + '\'' +
                ", primaryKeys='" + primaryKeys + '\'' +
                ", uniqueKeys='" + uniqueKeys + '\'' +
                ", source='" + source + '\'' +
                ", enabled=" + enabled +
                "} " + super.toString();
    }
}
