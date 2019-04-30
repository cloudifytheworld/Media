package com.huawei.mfg.pool;

import com.huawei.mfg.conf.MfgConfiguration;
import com.huawei.mfg.conf.RestfulClientConfiguration;

import static com.huawei.mfg.conf.RestfulClientConfiguration.MFG_ETL_REST_HOSTS;
import static com.huawei.mfg.conf.RestfulClientConfiguration.MFG_ETL_REST_MEDIA_TYPE;
import static com.huawei.mfg.conf.RestfulClientConfiguration.MFG_ETL_SAMPLE_DATA_DIR;

final public class RestDatasourceFactory {
    private RestDatasourceFactory() {}

    public static RestfulClientConfiguration createRestfulClientConfiguration(MfgConfiguration config) {
        RestfulClientConfiguration conf = RestfulClientConfiguration.create();

        if (config.containsKey(MFG_ETL_REST_HOSTS)) {
            conf.setStringProperty(MFG_ETL_REST_HOSTS, config.getString(MFG_ETL_REST_HOSTS));
        }

        if (config.containsKey(MFG_ETL_SAMPLE_DATA_DIR)) {
            conf.setStringProperty(MFG_ETL_SAMPLE_DATA_DIR, config.getString(MFG_ETL_SAMPLE_DATA_DIR));
        }

        if (config.containsKey(MFG_ETL_REST_MEDIA_TYPE)) {
            conf.setStringProperty(MFG_ETL_REST_MEDIA_TYPE, config.getString(MFG_ETL_REST_MEDIA_TYPE));
        }

        return conf;
    }

    public static RestDatasource createRestDatasource(RestfulClientConfiguration config) {
        RestDatasource restDatasource = new RestDatasource(config);
        restDatasource.connect();
        return restDatasource;
    }

}
