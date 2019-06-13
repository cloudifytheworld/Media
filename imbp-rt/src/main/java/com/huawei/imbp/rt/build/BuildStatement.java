package com.huawei.imbp.rt.build;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.google.common.base.Throwables;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Charles(Li) Cai
 * @date 6/11/2019
 */

@Component
@Log4j2
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
