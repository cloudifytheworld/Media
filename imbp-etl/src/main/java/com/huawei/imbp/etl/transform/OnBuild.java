package com.huawei.imbp.etl.transform;

import org.springframework.data.redis.core.ReactiveRedisTemplate;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */
@FunctionalInterface
public interface OnBuild {

    void onIndexBuild(ReactiveRedisTemplate<String, String> reactiveRedisTemplate);
}
