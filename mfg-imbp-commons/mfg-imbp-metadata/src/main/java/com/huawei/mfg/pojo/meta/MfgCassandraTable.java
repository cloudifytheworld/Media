package com.huawei.mfg.pojo.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfgCassandraTable {
    private String table;
    private String keyspace;
    private Map<String, String> srcTargetColumnMap; //source column to target column
    private Map<String, String> targetSrcColumnMap; //target column to source column;
    private List<String> primaryKeys;
    private List<String> partitionKeys;
    private List<String> clusteringKeys;

    public MfgCassandraTable() {
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public Map<String, String> getSrcTargetColumnMap() {
        return srcTargetColumnMap;
    }

    public void putSrcTargetColumn(String src, String target) {
        if (this.srcTargetColumnMap == null) this.srcTargetColumnMap = new HashMap<>();
        this.srcTargetColumnMap.put(src, target);
    }

    public Map<String, String> getTargetSrcColumnMap() {
        return targetSrcColumnMap;
    }

    public void putTargetSrcColumn(String target, String src) {
        if (this.targetSrcColumnMap == null) this.targetSrcColumnMap = new HashMap<>();
        this.targetSrcColumnMap.put(target, src);
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void addPrimaryKey(String primaryKey) {
        if (this.primaryKeys == null) this.primaryKeys = new ArrayList<>();
        this.primaryKeys.add(primaryKey);
    }

    public void addPrimaryKeys(List<String> primaryKeys) {
        if (this.primaryKeys == null) this.primaryKeys = new ArrayList<>();
        this.primaryKeys.addAll(primaryKeys);
    }

    public List<String> getPartitionKeys() {
        return partitionKeys;
    }

    public void setPartitionKeys(List<String> partitionKeys) {
        this.partitionKeys = partitionKeys;
    }

    public List<String> getClusteringKeys() {
        return clusteringKeys;
    }

    public void setClusteringKeys(List<String> clusteringKeys) {
        this.clusteringKeys = clusteringKeys;
    }

    @Override
    public String toString() {
        return "MfgCassandraTable{" +
                "table='" + table + '\'' +
                ", keyspace='" + keyspace + '\'' +
                ", srcTargetColumnMap=" + srcTargetColumnMap +
                ", targetSrcColumnMap=" + targetSrcColumnMap +
                ", primaryKeys=" + primaryKeys +
                ", partitionKeys=" + partitionKeys +
                ", clusteringKeys=" + clusteringKeys +
                '}';
    }

}
