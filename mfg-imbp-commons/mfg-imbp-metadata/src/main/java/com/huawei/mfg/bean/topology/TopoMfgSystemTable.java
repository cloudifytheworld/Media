package com.huawei.mfg.bean.topology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.huawei.mfg.bean.MfgSystemTable;
import com.huawei.mfg.bean.MfgSystemTableColumn;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "createdBy", "updatedBy", "lastUpdated", "created" })
public class TopoMfgSystemTable extends MfgSystemTable {
    private List<MfgSystemTableColumn> columns;

    public TopoMfgSystemTable() {
    }

    public List<MfgSystemTableColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<MfgSystemTableColumn> columns) {
        this.columns = columns;
    }

    public void addColumn(MfgSystemTableColumn column) {
        if (this.columns == null) this.columns = new ArrayList<>();
        this.columns.add(column);
    }

    @Override
    public String toString() {
        return "TopoMfgSystemTable{" +
                "columns=" + columns +
                "} " + super.toString();
    }

}
