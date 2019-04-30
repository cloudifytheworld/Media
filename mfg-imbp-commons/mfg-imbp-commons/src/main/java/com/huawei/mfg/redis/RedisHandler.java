package com.huawei.mfg.redis;

import com.huawei.mfg.util.Pair;

import java.util.List;
import java.util.Map;

public interface RedisHandler {

	public List<String> fetchKeys(String keyPattern);

	public Map<String, Object> fetchHashByKey(String key);

	public <R> Pair<Map<String, String>, Object> fetchHashByKey(String key, Class<R> clazz);

	public <R> Map<String, Pair<Map<String, String>, Object>> fetchHashesByKeys(List<String> keys, Class<R> clazz);

	public List<String> fetchList(String key);

	public List<String> fetchSet(String key);

	public List<String> fetchZset(String key);

}
