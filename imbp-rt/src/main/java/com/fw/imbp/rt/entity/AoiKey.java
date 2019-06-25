package com.fw.imbp.rt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Charles(Li) Cai
 * @date 5/13/2019
 */

@PrimaryKeyClass
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AoiKey implements Serializable {

    @PrimaryKeyColumn(name="created_day", type= PrimaryKeyType.PARTITIONED)
    private String createdDay
            ;
    @PrimaryKeyColumn(name="device_type", type= PrimaryKeyType.PARTITIONED)
    private String deviceType;

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private int hour;

    @PrimaryKeyColumn(name="mins", type = PrimaryKeyType.PARTITIONED)
    private int minute;

    @PrimaryKeyColumn(name="created_time", type = PrimaryKeyType.CLUSTERED)
    private Date createdTime;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    private String label;

}
