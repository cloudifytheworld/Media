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

    @Autowired
    public Session cassandraSession;

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    Map<OnSystem, KeySpaceTable> systemKeySpaceTable = new HashMap<>();

    @PostConstruct
    public void init(){

        try {
            KeySpaceTable aoi = new KeySpaceTable();
            aoi.setKeySpace("images");
            aoi.setTable("aoi_single_component_image_1");

            systemKeySpaceTable.put(OnSystem.valueOf("aoi"), aoi);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
        }
    }

    public abstract PreparedStatement build(String system) throws Exception;
    public abstract Set<String> getIndex(String system, String date, long startTime, long endTime);
    public abstract BoundStatement bind(String[] keys, PreparedStatement prepStmt);

    public String getSpaceAndTable(String system) {

        KeySpaceTable keySpaceTable = systemKeySpaceTable.get(OnSystem.valueOf(system));
        String keySpace = keySpaceTable.getKeySpace();
        String table = keySpaceTable.getTable();
        String spaceTable = keySpace+"."+table;

        return spaceTable;
    }


}
