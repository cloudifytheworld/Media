package com.huawei.imbp.etl.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Charles(Li) Cai
 * @date 5/13/2019
 */

@PrimaryKeyClass
@Data
public class AoiKey implements Serializable {

    @PrimaryKeyColumn(name="created_day", type= PrimaryKeyType.PARTITIONED)
    private String createdDay
            ;
    @PrimaryKeyColumn(name="device_type", type= PrimaryKeyType.PARTITIONED)
    private String deviceType;

    @PrimaryKeyColumn(name="hour", type = PrimaryKeyType.PARTITIONED)
    private int hour;

    @PrimaryKeyColumn(name="mins", type = PrimaryKeyType.PARTITIONED)
    private int minute;

    @PrimaryKeyColumn(name="sec", type = PrimaryKeyType.PARTITIONED)
    private int second;

    @PrimaryKeyColumn(name="created_time", type = PrimaryKeyType.CLUSTERED)
    private Timestamp createdTime;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    private String label;

}
