package com.huawei.imbp.etl.build;

import org.springframework.data.redis.core.ReactiveRedisTemplate;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */
@FunctionalInterface
public interface OnIndexBuild {

    void onIndexBuild(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) throws Exception;
}
