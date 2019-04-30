package com.huawei.mfg.datasource;

import com.google.common.base.Strings;
import com.huawei.mfg.conf.MfgConfiguration;
import com.huawei.mfg.conf.MfgJDBCDatasinkConfiguration;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.MfgConfigurationException;

import java.sql.SQLException;

import static com.huawei.mfg.util.CommonConstants.*;

final public class JDBCDatasourceFactory {

    private JDBCDatasourceFactory() {}

    public static MfgJDBCDatasourceConfiguration createJdbcDatasourceConfiguration(MfgConfiguration config, String prefix) {
        MfgJDBCDatasourceConfiguration conf = new MfgJDBCDatasourceConfiguration();
        setupJdbcConfig(config, conf, prefix);
        return conf;
    }

    public static MfgJDBCDatasinkConfiguration createJdbcDatasinkConfiguration(MfgConfiguration config, String prefix) {
        MfgJDBCDatasinkConfiguration conf = new MfgJDBCDatasinkConfiguration();
        setupJdbcConfig(config, conf, prefix);
        return conf;
    }

    public static SQLDatasource createJdbcDatasource(MfgConfiguration config, String prefix) throws MfgConfigurationException {
        SQLDatasource ds = null;
        try {
            MfgJDBCDatasourceConfiguration dsConfig = createJdbcDatasourceConfiguration(config, prefix);
            ds = new SQLDatasource(dsConfig);
            ds.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MfgConfigurationException("Exception caught when creating SQLDatasource", e);
        }
        return ds;
    }

    public static SQLDatasource createJdbcDatasource(MfgJDBCDatasourceConfiguration dsConfig) throws MfgConfigurationException {
        SQLDatasource ds = null;
        try {
            ds = new SQLDatasource(dsConfig);
            ds.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MfgConfigurationException("Exception caught when creating SQLDatasource", e);
        }
        return ds;
    }

    private static void setupJdbcConfig(MfgConfiguration config, MfgJDBCDatasourceConfiguration conf, String prefix) {
        if (Strings.isNullOrEmpty(prefix)) {
            conf.setPoolName(config.getString(JDBC_POOL_NAME));
            conf.setUsername(config.getString(JDBC_DB_USERNAME));
            conf.setPassword(config.getString(JDBC_DB_PASSWORD));
            conf.setDatasourceClassname(config.getString(JDBC_DB_DATASOURCE_CLASS));

            if (config.containsKey(JDBC_MYSQL_TEST_QUERY)) {
                conf.setConnectionTestQuery(config.getString(JDBC_MYSQL_TEST_QUERY));
            }

            conf.setHost(config.getString(JDBC_DB_HOST));
            conf.setPort(config.getInteger(JDBC_DB_PORT));

            if (config.containsKey(JDBC_DB_NAME)) {
                conf.setDatabaseName(config.getString(JDBC_DB_NAME));
            }

            if (config.containsKey(JDBC_DB_PROPERTIES)) {
                conf.setProperties(config.getString(JDBC_DB_PROPERTIES));
            }

            if (config.containsKey(ORACLE_SID)) {
                conf.setOracleServiceId(config.getString(ORACLE_SID));
            }

            if (config.containsKey(ORACLE_SERVICE_NAME)) {
                conf.setOracleServiceName(config.getString(ORACLE_SERVICE_NAME));
            }
        }
        else {
            conf.setPoolName(config.getString(String.format("%s.%s", prefix, JDBC_POOL_NAME)));
            conf.setUsername(config.getString(String.format("%s.%s", prefix, JDBC_DB_USERNAME)));
            conf.setPassword(config.getString(String.format("%s.%s", prefix, JDBC_DB_PASSWORD)));
            conf.setDatasourceClassname(config.getString(String.format("%s.%s", prefix, JDBC_DB_DATASOURCE_CLASS)));

            String prop = String.format("%s.%s", prefix, JDBC_MYSQL_TEST_QUERY);
            if (config.containsKey(prop)) {
                conf.setConnectionTestQuery(config.getString(prop));
            }

            conf.setHost(config.getString(String.format("%s.%s", prefix, JDBC_DB_HOST)));
            conf.setPort(config.getInteger(String.format("%s.%s", prefix, JDBC_DB_PORT)));

            prop = String.format("%s.%s", prefix, JDBC_DB_NAME);
            if (config.containsKey(prop)) {
                conf.setDatabaseName(config.getString(prop));
            }

            prop = String.format("%s.%s", prefix, JDBC_DB_PROPERTIES);
            if (config.containsKey(prop)) {
                conf.setProperties(config.getString(prop));
            }

            prop = String.format("%s.%s", prefix, ORACLE_SID);
            if (config.containsKey(prop)) {
                conf.setOracleServiceId(config.getString(prop));
            }

            prop = String.format("%s.%s", prefix, ORACLE_SERVICE_NAME);
            if (config.containsKey(prop)) {
                conf.setOracleServiceName(config.getString(prop));
            }
        }
    }

}
