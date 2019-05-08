package com.huawei.imbp.imbprt.entity;

import com.datastax.driver.core.Row;

import java.util.List;

/**
 * @author Charles(Li) Cai
 * @date 4/30/2019
 */
public class RowsKey {

    List<Row> rows;
    String key;
    String hour;
    int which;

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getWhich() {
        return which;
    }

    public void setWhich(int which) {
        this.which = which;
    }
}
