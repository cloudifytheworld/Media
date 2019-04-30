package com.huawei.mfg.pojo.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfgProduct {
    private String product;

    @JsonProperty("sources")
    private List<MfgProductTable> sourceTables;

    public MfgProduct() {
    }

    public MfgProduct(String product) {
        this.product = product;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public List<MfgProductTable> getSourceTables() {
        return sourceTables;
    }

    public void addSourceTable(MfgProductTable sourceTable) {
        if (this.sourceTables == null) this.sourceTables = new ArrayList<>();
        this.sourceTables.add(sourceTable);
    }

    @Override
    public String toString() {
        return "MfgProduct{" +
                "product='" + product + '\'' +
                ", sourceTables=" + sourceTables +
                '}';
    }

}

