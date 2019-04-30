package com.huawei.mfg.pojo.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfgProductTable {
    private String table;
    private List<String> columns;

    @JsonProperty("cass_table")
    private MfgCassandraTable cassandraTable;

    public MfgProductTable() {
    }

    public MfgProductTable(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void addColumn(String column) {
        if (this.columns == null) this.columns = new ArrayList<>();
        this.columns.add(column);
    }

    public MfgCassandraTable getCassandraTable() {
        return cassandraTable;
    }

    public void setCassandraTable(MfgCassandraTable cassandraTable) {
        this.cassandraTable = cassandraTable;
    }

    @Override
    public String toString() {
        return "MfgProductTable{" +
                "table='" + table + '\'' +
                ", columns=" + columns +
                ", cassandraTable=" + cassandraTable +
                '}';
    }

}
