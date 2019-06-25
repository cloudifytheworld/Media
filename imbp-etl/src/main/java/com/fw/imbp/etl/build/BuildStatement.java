package com.fw.imbp.etl.build;

import com.datastax.driver.core.querybuilder.Insert;
import com.google.common.base.Throwables;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 6/11/2019
 */

@Component
@Log4j2
public abstract class BuildStatement {

    OnIndexBuild onIndexBuild;
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

    public abstract Insert build(Map<String, Object> payload, String system) throws Exception;
    public void index(OnIndexBuild onIndexBuild){
        this.onIndexBuild = onIndexBuild;
    }

    public String buildIndex(ReactiveRedisTemplate<String, String> redisTemplate) throws Exception{
        return this.onIndexBuild.onIndexBuild(redisTemplate);
    }

}
