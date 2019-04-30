package com.huawei.mfg.redis;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.huawei.mfg.pool.JedisConnector;
import com.huawei.mfg.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanResult;

import java.util.*;

public class RedisClusterHandler extends BaseRedisHandler {
	private Logger logger = LoggerFactory.getLogger(RedisClusterHandler.class);

	public RedisClusterHandler(JedisConnector jedisConnection) {
		super(jedisConnection);
	}

	public List<String> fetchKeys(String keyPattern) {
		JedisCluster jedisCluster = this.jedisConnection.getJedisCluster();
		Preconditions.checkNotNull(jedisCluster, "Redis cluster is not established");

		Set<String> keySet = new HashSet<>();
		Map<String, JedisPool> pools = jedisCluster.getClusterNodes();
		final Set<String> poolKeys = new HashSet<>();
		
		if (!pools.isEmpty()) {
			pools.forEach( (k, pool) -> {
				if (k.contains(":")) {
					k = k.substring(0, k.indexOf(":"));
				}
				
				if (!poolKeys.contains(k)) {
					poolKeys.add(k);
					
					Jedis jedis = pool.getResource();
					Set<byte[]> byteKeys = jedis.keys(keyPattern.getBytes());
					jedis.close();
					List<byte[]> list = new ArrayList<>(byteKeys);

					for (int i = 0; i < list.size(); i++) {
						keySet.add(new String(list.get(i)));
					}
				}
			});
		}

    	return Lists.newArrayList(keySet);
	}

	public Map<String, Object> fetchHashByKey(String key) {
		JedisCluster jedisCluster = this.jedisConnection.getJedisCluster();
		Preconditions.checkNotNull(jedisCluster, "Redis cluster is not established");

		ScanResult<Map.Entry<String, String>> result = jedisCluster.hscan(key, "0");
		return this.toMap(result);
	}

	public <R> Pair<Map<String, String>, Object> fetchHashByKey(String key, Class<R> clazz) {
		JedisCluster jedisCluster = this.jedisConnection.getJedisCluster();
		Preconditions.checkNotNull(jedisCluster, "Redis cluster is not established");

		ScanResult<Map.Entry<String, String>> result = jedisCluster.hscan(key, "0");
		return this.toMapObject(result, clazz);
	}

	public <R> Map<String, Pair<Map<String, String>, Object>> fetchHashesByKeys(List<String> keys, Class<R> clazz) {
		JedisCluster jedisCluster = this.jedisConnection.getJedisCluster();
		Preconditions.checkNotNull(jedisCluster, "Redis cluster is not established");

		Map<String, Pair<Map<String, String>, Object>> keyToMap = new HashMap<>();

		int count = 0;
		for (String key : keys) {
			ScanResult<Map.Entry<String, String>> result = jedisCluster.hscan(key, "0");
			List<Map.Entry<String, String>> entries = result.getResult();
				
			if (entries != null && !entries.isEmpty()) {
				Map<String, String> map = new HashMap<>();
		
				entries.forEach(e -> {
					map.put(e.getKey(), e.getValue());
				});
		
				R dst = mapper.convertValue(map, clazz);
				keyToMap.put(key, Pair.of(map, dst));
				count++;
				
				if (count % 100 == 0) {
					this.logger.debug("Converting {} map toMapObject {} ...", count, clazz.getSimpleName());
				}
			}
			else {
				System.err.println("Empty hash!");
			}
		}

		return keyToMap;
	}

	public void persist(Map<String, Pair<Map<String, String>, Object>> keyToMap) {
		keyToMap.forEach((k, v) -> {
			this.persist(k, v.getFirst());
		});
	}
	
	public void persist(String key, Map<String, String> hash) {
		JedisCluster jedisCluster = this.jedisConnection.getJedisCluster();
		Preconditions.checkNotNull(jedisCluster, "Redis cluster is not established");

		this.logger.debug("   Setting {} into cluster ...", key);
	
		//pipeline can be used here
		jedisCluster.hmset(key, hash);
	}

	public List<String> fetchList(String key) {
		JedisCluster jedisCluster = this.jedisConnection.getJedisCluster();
		Preconditions.checkNotNull(jedisCluster, "Redis cluster is not established");

		List<String> values = jedisCluster.lrange(key, 0, -1);
		logger.debug("{}", values);
		return values;
	}

	public List<String> fetchSet(String key) {
		JedisCluster jedisCluster = this.jedisConnection.getJedisCluster();
		Preconditions.checkNotNull(jedisCluster, "Redis cluster is not established");

		Set<String> values = jedisCluster.smembers(key);
		logger.debug("{}", values);
		return new ArrayList<>(values);
	}

	public List<String> fetchZset(String key) {
		JedisCluster jedisCluster = this.jedisConnection.getJedisCluster();
		Preconditions.checkNotNull(jedisCluster, "Redis cluster is not established");

		Set<String> values = jedisCluster.zrange(key, 0, -1);
		logger.debug("{}", values);
		return new ArrayList<>(values);
	}

}
