package com.huawei.mfg.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class BaseBean implements Serializable {
    private int id;
    private String createdBy;
    private String updatedBy;
    private Timestamp lastUpdated;
    private Timestamp created;

    public BaseBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "id=" + id +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", created=" + created +
                '}';
    }

}
