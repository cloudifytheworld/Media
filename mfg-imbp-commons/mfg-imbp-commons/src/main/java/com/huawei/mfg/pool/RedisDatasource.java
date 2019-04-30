package com.huawei.mfg.pool;

import com.huawei.mfg.conf.MfgRedisDatasourceConfiguration;
import com.huawei.mfg.util.MfgException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;


/**
 * Wrapper of JedisConnector
 */
public class RedisDatasource {
    private MfgRedisDatasourceConfiguration config;
    private JedisConnector jedisConnector;

    public RedisDatasource(MfgRedisDatasourceConfiguration config) {
        this.config = config;
    }

    public void connect() throws MfgException {
        this.jedisConnector = new JedisConnector(this.config);
        this.jedisConnector.connect();
    }

    public JedisConnector getJedisConnector() {
        return this.jedisConnector;
    }

    public Jedis getSingleNodeJedis() { return this.jedisConnector.getSingleNodeJedis(); }

    public JedisCluster getJedisCluster() { return this.jedisConnector.getJedisCluster(); }

    public void close() throws IOException {
        if (this.jedisConnector != null) {
            this.jedisConnector.close();
        }
    }

}
