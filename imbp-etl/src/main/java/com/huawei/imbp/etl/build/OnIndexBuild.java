package com.huawei.imbp.etl.build;

import org.springframework.data.redis.core.ReactiveRedisTemplate;

/**
 * @author Charles(Li) Cai
 * @date 6/05/2019
 */

@FunctionalInterface
public interface OnIndexBuild {

    public String onIndexBuild(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) throws Exception;
}
