package com.huawei.mfg.pool;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Preconditions;
import com.huawei.mfg.conf.MfgRedisDatasourceConfiguration;
import com.huawei.mfg.util.CommonConstants;
import com.huawei.mfg.util.MfgException;
import com.huawei.mfg.util.Pair;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;


public class JedisConnector {
	private MfgRedisDatasourceConfiguration config;
	private Jedis jedis;
	private JedisCluster jedisCluster;
	private AtomicBoolean connected;

	public JedisConnector(MfgRedisDatasourceConfiguration config) {
		this.config = config;
	}

	public void connect() throws MfgException {
		Preconditions.checkNotNull(this.config, "Redis configuration must be specified");

		if (this.config.getClusterMode().equalsIgnoreCase("single")) {
			String host = this.config.getSingleNodeHost();
			int port = this.config.getSingleNodePort();
			this.jedis = new Jedis(host, port);
		}
		else if (this.config.getClusterMode().equalsIgnoreCase("cluster")) {
			Set<HostAndPort> clusterNodes = new HashSet<>();
			int nodes = this.config.getClusterNodes();
			for (int i = 1; i <= nodes; i++) {
				Pair<String, Integer> pair = this.config.getClusterNode(i);
				clusterNodes.add(new HostAndPort(pair.getFirst(), pair.getSecond()));
			}
			this.jedisCluster = new JedisCluster(clusterNodes);
		}
		else {
			throw new MfgException("Redis cluster mode can only be configured as either 'single' or 'cluster'");
		}
	}

	public Jedis getSingleNodeJedis() { return this.jedis; }

	public JedisCluster getJedisCluster() { return this.jedisCluster; }

	public void close() throws IOException {
		if (this.jedisCluster != null) {
			this.jedisCluster.close();
		}

		if (this.jedis != null) {
			this.jedis.close();
		}
	}

}
