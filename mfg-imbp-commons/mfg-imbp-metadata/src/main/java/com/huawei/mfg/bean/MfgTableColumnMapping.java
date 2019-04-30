package com.huawei.mfg.bean;

import java.io.Serializable;

public class MfgTableColumnMapping extends BaseBean implements Serializable {
    private String columnName;
    private String abbrColumnName;
    private String conciseAbbrColumnName;
    private String description;

    public MfgTableColumnMapping() {
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getAbbrColumnName() {
        return abbrColumnName;
    }

    public void setAbbrColumnName(String abbrColumnName) {
        this.abbrColumnName = abbrColumnName;
    }

    public String getConciseAbbrColumnName() {
        return conciseAbbrColumnName;
    }

    public void setConciseAbbrColumnName(String conciseAbbrColumnName) {
        this.conciseAbbrColumnName = conciseAbbrColumnName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "MfgTableColumnMapping{" +
                "columnName='" + columnName + '\'' +
                ", abbrColumnName='" + abbrColumnName + '\'' +
                ", conciseAbbrColumnName='" + conciseAbbrColumnName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
