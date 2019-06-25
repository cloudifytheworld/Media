package com.fw.imbp.etl.build;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.fw.imbp.etl.common.DataType;
import com.fw.imbp.etl.common.ImbpException;
import com.fw.imbp.etl.validation.DataTypeValidation;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 6/11/2019
 */

@Component
@Log4j2
public class AoiBuildStatement extends BuildStatement {


    @Override
    public Insert build(Map<String, Object> payload, String system) throws Exception{

        //Todo All primary throw exception when null
        KeySpaceTable keySpaceTable = systemKeySpaceTable.get(OnSystem.valueOf(system));
        Insert insert = QueryBuilder.insertInto(keySpaceTable.keySpace, keySpaceTable.table);

        Object created_day = payload.get("created_day");
        insert.value("created_day", created_day);

        Object device_type = payload.get("device_type");
        insert.value("device_type", device_type);

        String createdTime = (String)payload.get("created_time");

        Long mills = (Long) DataTypeValidation.checkDataType(DataType.TIMESTAMP, createdTime);
        Timestamp timestamp = new Timestamp(mills);
        DateTime dateTime = new DateTime(timestamp.getTime());

        int hour = dateTime.getHourOfDay();
        insert.value("hour", hour);

        int minute = dateTime.getMinuteOfHour();
        insert.value("mins", minute);

        int second = dateTime.getSecondOfMinute();
        insert.value("sec", second);

        Object label = payload.get("label");
        insert.value("label", label);

        insert.value("created_time",timestamp);

        String primaryKey = device_type+"#"+hour +"#" + minute+"#"+second
                + "#" + label + "#" + mills;
        String partitionKey = created_day+"#"+device_type+"#"+hour+"#"+minute+"#"+second;
        String clusterKey = label+"#"+mills;

        //columns
        insert.value("board_id", payload.get("board_id"));
        insert.value("board_loc", payload.get("board_loc"));
        insert.value("extension", payload.get("extension"));
        insert.value("file_name", payload.get("file_name"));
        insert.value("product_type", payload.get("product_type"));

        String imageStr = (String)payload.get("image");
        ByteBuffer image = (ByteBuffer)DataTypeValidation.checkDataType(DataType.BLOB, imageStr);
        insert.value("image", image);

        index((redisTemplate) ->{
            try {
                redisTemplate.opsForZSet().add("secDate" + ":" + system + ":" + created_day,
                        partitionKey+"#"+clusterKey, mills).subscribe();
                redisTemplate.opsForSet().add("date" + ":" + system + ":" + created_day,
                        partitionKey).subscribe();
                String key = system + ":" + created_day+":"+primaryKey;
                return key;
            }catch (Exception e){;
                log.error(system + ":" + created_day+":"+primaryKey+"---"+e.getMessage());
                throw new ImbpException().setMessage("Can't insert index for "+primaryKey+"--"+e.getMessage());
            }
        });

        return insert;
    }
}
