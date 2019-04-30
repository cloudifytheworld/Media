package com.huawei.mfg.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetadataTableDef {
    private TableDef tableDef;
    private String hbaseRowKeyColumns;
    private String hbaseColumnFamilies;

    @JsonCreator
    private MetadataTableDef(@JsonProperty("tableDef") TableDef tableDef,
                             @JsonProperty("hbaseRowKeyColumns") String hbaseRowKeyColumns,
                             @JsonProperty("hbaseColumnFamilies") String hbaseColumnFamilies)
    {
        this.tableDef = tableDef;
        this.hbaseRowKeyColumns = hbaseRowKeyColumns;
        this.hbaseColumnFamilies = hbaseColumnFamilies;
    }

    public static MetadataTableDef create(TableDef tableDef, String hbaseRowKeyColumns, String hbaseColumnFamilies) {
        return new MetadataTableDef(tableDef, hbaseRowKeyColumns, hbaseColumnFamilies);
    }

    public TableDef getTableDef() {
        return tableDef;
    }

    public String getHbaseRowKeyColumns() {
        return hbaseRowKeyColumns;
    }

    public String getHbaseColumnFamilies() {
        return hbaseColumnFamilies;
    }

    @Override
    public String toString() {
        return "MetadataTableDef{" +
                "tableDef=" + tableDef +
                ", hbaseRowKeyColumns='" + hbaseRowKeyColumns + '\'' +
                ", hbaseColumnFamilies='" + hbaseColumnFamilies + '\'' +
                '}';
    }

}
