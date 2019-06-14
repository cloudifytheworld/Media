package com.huawei.imbp.rt.build;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

/**
 * @author Charles(Li) Cai
 * @date 6/11/2019
 */

public abstract class BuildStatement {

    public static final String aoiKeySpaceTable = "images.aoi_single_component_image_1";

    @Autowired
    public Session cassandraSession;

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    public abstract void init();
    public abstract Set<String> getIndex(String system, String date, long startTime, long endTime);
    public abstract BoundStatement bind(String[] keys);

}
