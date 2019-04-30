package com.huawei.mfg.datasource;

import com.google.common.base.Preconditions;
import com.huawei.mfg.conf.MfgConfiguration;
import com.huawei.mfg.conf.MfgRedisDatasourceConfiguration;
import com.huawei.mfg.pool.JedisConnector;
import com.huawei.mfg.pool.RedisDatasource;
import com.huawei.mfg.util.MfgException;

import static com.huawei.mfg.util.CommonConstants.*;

/**
 *
 * Redis Datasource factory which provides ways of creating Redis configuration based on MfgConfiguration
 * and RedisDatasource
 *
 */
final public class RedisDatasourceFactory {

    private RedisDatasourceFactory() {
    }

    public static MfgRedisDatasourceConfiguration createRedisDataSourceConfiguration(MfgConfiguration config) throws MfgException {
        MfgRedisDatasourceConfiguration conf = new MfgRedisDatasourceConfiguration();

        String clusterMode = config.getString(REDIS_CLUSTER_MODE);
        Preconditions.checkNotNull(clusterMode, "Redis cluster mode must be configured");

        conf.setClusterMode(clusterMode);
        if (clusterMode.equalsIgnoreCase("single")) {
            conf.setSingleNodeHost(config.getString(REDIS_SINGLE_NODE_HOST));
            conf.setSingleNodePort(config.getInteger(REDIS_SINGLE_NODE_PORT));
        }
        else if (clusterMode.equalsIgnoreCase("cluster")) {
            Integer nodes = config.getInteger(REDIS_CLUSTER_NODES);
            Preconditions.checkNotNull(nodes, "# of Redis cluster nodes must be configured");

            conf.setClusterNodes(nodes);
            for (int i = 1; i <= nodes; i++) {
                String prop = REDIS_CLUSTER_HOST_PREFIX + "." + i;
                String host = null;
                if (config.containsKey(prop)) {
                    host = config.getString(prop);
                }
                else {
                    throw new MfgException(String.format("'%s' is not configured", prop));
                }

                int port = -1;
                prop = REDIS_CLUSTER_PORT_PREFIX + "." + i;
                if (config.containsKey(prop)) {
                    port = config.getInteger(prop);
                }
                else {
                    throw new MfgException(String.format("'%s' is not configured", prop));
                }

                conf.addClusterNode(i, host, port);
            }
        }
        else {
            throw new MfgException("Redis cluster mode can only be configured as either 'single' or 'cluster'");
        }

        return conf;
    }

    /**
     * create an instance of JedisConnector directly from MfgRedisDatasourceConfiguration
     *
     * @param dsConfig
     * @return
     * @throws MfgException
     */
    public static JedisConnector createRedisConnector(MfgRedisDatasourceConfiguration dsConfig) throws MfgException {
        JedisConnector jedisConnector = new JedisConnector(dsConfig);
        jedisConnector.connect();
        return jedisConnector;
    }

    /**
     * create an instance of RedisDatasource
     *
     * @param dsConfig
     * @return
     * @throws MfgException
     */
    public static RedisDatasource createRedisDatasource(MfgRedisDatasourceConfiguration dsConfig) throws MfgException {
        RedisDatasource redisDatasource = new RedisDatasource(dsConfig);
        redisDatasource.connect();
        return redisDatasource;
    }

}
