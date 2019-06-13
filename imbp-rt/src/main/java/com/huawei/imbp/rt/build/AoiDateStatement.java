package com.huawei.imbp.rt.build;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * @author Charles(Li) Cai
 * @date 6/11/2019
 */

@Component
@Log4j2
public class AoiDateStatement extends BuildStatement {

    PreparedStatement prepStmt;

    @PostConstruct
    @Override
    public void init(){

        prepStmt = cassandraSession.prepare("SELECT * FROM "+aoiKeySpaceTable+" where created_day = ?" +
                " and device_type = ? and hour = ? and mins = ? and sec = ? ALLOW FILTERING");
    }

    @Override
    public Set<String> getIndex(String system, String date, long startTime, long endTime) {
        return redisTemplate.boundSetOps("date:" + system + ":" + date).members();
    }

    @Override
    public BoundStatement bind(String[] keys) {
        return this.prepStmt.bind(keys[0], keys[1], Integer.parseInt(keys[2]), Integer.parseInt(keys[3]),
                Integer.parseInt(keys[4]));
    }
}
