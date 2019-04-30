package com.huawei.mfg.redis;

import com.google.common.base.Preconditions;
import com.huawei.mfg.pool.JedisConnector;
import com.huawei.mfg.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

import java.util.*;

public class RedisStandaloneHandler extends BaseRedisHandler {
	private Logger logger = LoggerFactory.getLogger(RedisStandaloneHandler.class);

	public RedisStandaloneHandler(JedisConnector jedisConnection) {
		super(jedisConnection);
	}

	public List<String> fetchKeys(String keyPattern) {
		Jedis jedis = this.jedisConnection.getSingleNodeJedis();
		Preconditions.checkNotNull(jedis, "Redis connection is not established");

		List<String> keys = new ArrayList<>();
		jedis.auth("huawei123");

		// fetch all matching keys
		Set<byte[]> byteKeys = jedis.keys(keyPattern.getBytes());
		byteKeys.forEach(k-> {
			keys.add(new String(k));
		});

//		this.logger.debug("# of keys: {}", keys.size());

    	return keys;
	}

	public Map<String, Object> fetchHashByKey(String key) {
		Jedis jedis = this.jedisConnection.getSingleNodeJedis();
		Preconditions.checkNotNull(jedis, "Redis connection is not established");

		ScanResult<Map.Entry<String, String>> result = jedis.hscan(key, "0");
		return this.toMap(result);
	}

	public <R> Pair<Map<String, String>, Object> fetchHashByKey(String key, Class<R> clazz) {
		Jedis jedis = this.jedisConnection.getSingleNodeJedis();
		Preconditions.checkNotNull(jedis, "Redis connection is not established");

		ScanResult<Map.Entry<String, String>> result = jedis.hscan(key, "0");
		return this.toMapObject(result, clazz);
	}

	public <R> Map<String, Pair<Map<String, String>, Object>> fetchHashesByKeys(List<String> keys, Class<R> clazz) {
		Jedis jedis = this.jedisConnection.getSingleNodeJedis();
		Preconditions.checkNotNull(jedis, "Redis connection is not established");

		Map<String, Pair<Map<String, String>, Object>> keyToMap = new HashMap<>();
		int count = 0;

		for (String key : keys) {
			ScanResult<Map.Entry<String, String>> result = jedis.hscan(key, "0");

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
		}

		return keyToMap;
	}

	//list
	public List<String> fetchList(String key) {
		Jedis jedis = this.jedisConnection.getSingleNodeJedis();
		Preconditions.checkNotNull(jedis, "Redis connection is not established");

		List<String> values = jedis.lrange(key, 0, -1);
		//logger.debug("{}", values);
		return values;
	}

	//set
	public List<String> fetchSet(String key) {
		Jedis jedis = this.jedisConnection.getSingleNodeJedis();
		Preconditions.checkNotNull(jedis, "Redis connection is not established");

		Set<String> values = jedis.smembers(key);
		//logger.debug("{}", values);
		return new ArrayList<>(values);
	}

	//zset
	public List<String> fetchZset(String key) {
		Jedis jedis = this.jedisConnection.getSingleNodeJedis();
		Preconditions.checkNotNull(jedis, "Redis connection is not established");

		Set<String> values = jedis.zrange(key, 0, -1);
		//logger.debug("{}", values);
		return new ArrayList<>(values);
	}

}
