package com.huawei.mfg.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableDef {
    private String tableName;
    private String source;
    private List<ColumnDef> columnDefs;
    private List<String> primaryKeys;
    private String criteriaColumn;
    private List<String> uniqueKeys;

    @JsonCreator
    private TableDef(@JsonProperty("tableName") String tableName,
                     @JsonProperty("columnDefs") List<ColumnDef> columnDefs,
                     @JsonProperty("primaryKeys") List<String> primaryKeys,
                     @JsonProperty("uniqueKeys") List<String> uniqueKeys,
                     @JsonProperty("criteriaColumn") String criteriaColumn,
                     @JsonProperty("source") String source)
    {
        this.tableName = tableName;
        this.columnDefs = columnDefs;
        this.primaryKeys = primaryKeys;
        this.uniqueKeys = uniqueKeys;
        this.criteriaColumn = criteriaColumn;
        this.source = source;
    }

    public static TableDef create(String tableName,
                                  List<ColumnDef> columnDefs,
                                  List<String> primaryKeys,
                                  List<String> uniqueKeys,
                                  String criteriaColumn,
                                  String source) {
        return new TableDef(tableName, columnDefs, primaryKeys, uniqueKeys, criteriaColumn, source);
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnDef> getColumnDefs() {
        return columnDefs;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public List<String> getUniqueKeys() {
        return uniqueKeys;
    }

    public String getCriteriaColumn() {
        return criteriaColumn;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "TableDef{" +
                ", tableName='" + tableName + '\'' +
                ", source='" + source + '\'' +
                ", columnDefs=" + columnDefs +
                ", primaryKeys=" + primaryKeys +
                ", criteriaColumn='" + criteriaColumn + '\'' +
                ", uniqueKeys=" + uniqueKeys +
                '}';
    }
}
