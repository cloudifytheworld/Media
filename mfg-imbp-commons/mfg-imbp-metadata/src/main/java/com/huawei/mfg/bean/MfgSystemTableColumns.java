package com.huawei.mfg.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "createdBy", "updatedBy", "lastUpdated", "created" })
public class MfgSystemTableColumns extends MfgSystemTable implements Serializable {
    private List<MfgSystemTableColumn> columns;

    public MfgSystemTableColumns() {
    }

    public List<MfgSystemTableColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<MfgSystemTableColumn> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "MfgSystemTableColumns{" +
                "columns=" + columns +
                "} " + super.toString();
    }
}
