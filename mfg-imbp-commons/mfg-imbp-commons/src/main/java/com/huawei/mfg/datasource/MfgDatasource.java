package com.huawei.mfg.datasource;

/**
 * Datasource wrapper
 *
 */
public class MfgDatasource {
    private Object datasource;

    MfgDatasource(Object datasource) {
        this.datasource = datasource;
    }

    public static MfgDatasource create(Object datasource) {
        return new MfgDatasource(datasource);
    }

    public Object getDatasource() {
        return datasource;
    }

    @Override
    public String toString() {
        return "MfgDatasource{" +
                "datasource=" + datasource +
                '}';
    }

}
