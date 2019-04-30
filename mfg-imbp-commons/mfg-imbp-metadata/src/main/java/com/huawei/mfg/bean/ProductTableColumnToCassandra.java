package com.huawei.mfg.bean;

import java.io.Serializable;

/**
 * Bean for table 'product_table_column_to_cassandra'
 */
public class ProductTableColumnToCassandra extends BaseBean implements Serializable {
    private int columnId; //mfg_system_table_column id
    private int cassandraTableId; //mfg_system_table_to_cassandra id

    public ProductTableColumnToCassandra() {
    }

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public int getCassandraTableId() {
        return cassandraTableId;
    }

    public void setCassandraTableId(int cassandraTableId) {
        this.cassandraTableId = cassandraTableId;
    }

    @Override
    public String toString() {
        return "ProductTableColumnToCassandra{" +
                "columnId=" + columnId +
                ", cassandraTableId=" + cassandraTableId +
                "} " + super.toString();
    }
}
