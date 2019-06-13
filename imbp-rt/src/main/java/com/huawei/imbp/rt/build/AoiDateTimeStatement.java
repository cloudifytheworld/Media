package com.huawei.imbp.rt.build;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Set;

/**
 * @author Charles(Li) Cai
 * @date 6/11/2019
 */

@Component
@Log4j2
public class AoiDateTimeStatement extends BuildStatement {

    @Override
    public PreparedStatement build(String system) throws Exception {

        String spaceTable = getSpaceAndTable(system);
        return cassandraSession.prepare("SELECT * FROM "+spaceTable+" where created_day = ?" +
                " and device_type = ? and hour = ? and mins = ? and sec = ? and label = ? and created_time = ?");
    }

    @Override
    public Set<String> getIndex(String system, String date, long startTime, long endTime) {
        return redisTemplate.boundZSetOps("secDate:"+system + ":" + date).rangeByScore(startTime, endTime);
    }

    @Override
    public BoundStatement bind(String[] keys, PreparedStatement prepStmt) {
        return prepStmt.bind(keys[0], keys[1], Integer.parseInt(keys[2]), Integer.parseInt(keys[3]),
                Integer.parseInt(keys[4]), keys[5], new Timestamp(Long.parseLong(keys[6])));
    }
}
