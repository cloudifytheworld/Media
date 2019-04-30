package com.huawei.mfg.bean;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 *
 * SELECT d.* FROM mfg_system_table_column_mapping d
 * INNER JOIN mfg_system_table_column c ON c.id = d.col_id
 * INNER JOIN mfg_system_table a ON a.id = c.table_id
 * INNER JOIN mfg_system b ON b.name = 'test' AND b.id = a.system_id;
 *
 */
public class MfgSystem extends BaseBean implements Serializable {
    @NotNull
    private String name;

    public MfgSystem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MfgSystem{" +
                "name='" + name + '\'' +
                ", " + super.toString() +
                '}';
    }
}
