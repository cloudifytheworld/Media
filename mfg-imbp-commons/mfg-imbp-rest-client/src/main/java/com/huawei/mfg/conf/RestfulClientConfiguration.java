package com.huawei.mfg.conf;

import com.huawei.mfg.util.MfgConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestfulClientConfiguration extends MfgConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(RestfulClientConfiguration.class);
    public static final String DEFAULT_CONFIG_FILE           = "rest-client-config.properties";
    public static final String MFG_ETL_REST_HOSTS            = "mfg.etl.rest.hosts";
    public static final String MFG_ETL_SAMPLE_DATA_DIR       = "mfg.etl.sample.data.dir";
    public static final String MFG_ETL_REST_MEDIA_TYPE       = "mfg.etl.rest.media.type";
    public static final int DEFAULT_ETL_RESTFUL_SERVICE_PORT = 8080;

    private RestfulClientConfiguration() {
        this.setConfigFile(DEFAULT_CONFIG_FILE);
    }

    private RestfulClientConfiguration(String configFile) {
        super(configFile);
    }

    public static RestfulClientConfiguration create() {
        return new RestfulClientConfiguration();
    }

    public static RestfulClientConfiguration load(String configFile) throws MfgConfigurationException {
        RestfulClientConfiguration config = new RestfulClientConfiguration(configFile);
        config.initialize();
        return config;
    }

    public static void main(String[] args) {
        try {
            RestfulClientConfiguration config = RestfulClientConfiguration.load(DEFAULT_CONFIG_FILE);
            logger.debug("{}", config);
        } catch (MfgConfigurationException e) {
            e.printStackTrace();
        }
    }

}
