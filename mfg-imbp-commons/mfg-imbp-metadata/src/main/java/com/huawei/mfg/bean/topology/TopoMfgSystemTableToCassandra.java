package com.huawei.mfg.bean.topology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.huawei.mfg.bean.MfgSystemTableToCassandra;
import com.huawei.mfg.bean.ProductTableColumnToCassandraColumn;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "createdBy", "updatedBy", "lastUpdated", "created" })
public class TopoMfgSystemTableToCassandra extends MfgSystemTableToCassandra {
    private String cassTableName;
    private List<ProductTableColumnToCassandraColumn> columns;

    public TopoMfgSystemTableToCassandra() {
    }

    public String getCassTableName() {
        return cassTableName;
    }

    public void setCassTableName(String cassTableName) {
        this.cassTableName = cassTableName;
    }

    public List<ProductTableColumnToCassandraColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ProductTableColumnToCassandraColumn> columns) {
        this.columns = columns;
    }

    public void addColumn(ProductTableColumnToCassandraColumn column) {
        if (this.columns == null) this.columns = new ArrayList<>();
        this.columns.add(column);
    }

    @Override
    public String toString() {
        return "TopoMfgSystemTableToCassandra{" +
                "cassTableName='" + cassTableName + '\'' +
                ", columns=" + columns +
                "} " + super.toString();
    }
}
