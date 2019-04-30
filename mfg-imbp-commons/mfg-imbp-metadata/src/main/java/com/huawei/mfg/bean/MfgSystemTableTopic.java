package com.huawei.mfg.bean;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class MfgSystemTableTopic extends BaseBean implements Serializable {
    private int tableId;

    @NotNull
    private String topic;
    private boolean enabled;

    public MfgSystemTableTopic() {
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "MfgSystemTableTopic{" +
                "tableId=" + tableId +
                ", topic='" + topic + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
