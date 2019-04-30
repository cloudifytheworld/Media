package com.huawei.mfg.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.mfg.pool.JedisConnector;
import com.huawei.mfg.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ScanResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class BaseRedisHandler implements RedisHandler {
	private Logger logger = LoggerFactory.getLogger(BaseRedisHandler.class);
	protected JedisConnector jedisConnection;
	protected ObjectMapper mapper;

	public BaseRedisHandler(JedisConnector jedisConnection) {
		super();
		this.jedisConnection = jedisConnection;
		this.mapper = new ObjectMapper();
	}

	public Map<String, Object> toMap(ScanResult<Map.Entry<String, String>> result) {
		List<Map.Entry<String, String>> entries = result.getResult();

		Map<String, Object> map = new HashMap<>();
		if (entries != null && !entries.isEmpty()) {
			entries.forEach(e -> {
				map.put(e.getKey(), e.getValue());
			});
		}

		return map;
	}

	public <R> Pair<Map<String, String>, Object> toMapObject(ScanResult<Map.Entry<String, String>> result, Class<R> clazz) {
		List<Map.Entry<String, String>> entries = result.getResult();

		Pair<Map<String, String>, Object> pair = null;
		if (entries != null && !entries.isEmpty()) {
			Map<String, String> map = new HashMap<>();

			entries.forEach(e -> {
				map.put(e.getKey(), e.getValue());
			});

			R dst = mapper.convertValue(map, clazz);
			pair = Pair.of(map, dst);
		}
		else {
			System.err.println("Empty hash!");
		}
		return pair;
	}

}
