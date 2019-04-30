package com.huawei.mfg.datasource;

/**
 * Datasink wrapper
 *
 */
public class MfgDatasink extends MfgDatasource {

    MfgDatasink(Object datasink) {
        super(datasink);
    }

    public static MfgDatasink create(Object datasink) {
        return new MfgDatasink(datasink);
    }

    public Object getDatasink() {
        return this.getDatasource();
    }

    @Override
    public String toString() {
        return "MfgDatasink{" +
                "datasink=" + this.getDatasink() +
                '}';
    }

}
