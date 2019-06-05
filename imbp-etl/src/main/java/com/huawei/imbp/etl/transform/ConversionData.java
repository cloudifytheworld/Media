package com.huawei.imbp.etl.transform;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.common.base.Throwables;
import com.huawei.imbp.etl.common.DataType;
import com.huawei.imbp.etl.entity.AoiEntity;
import com.huawei.imbp.etl.entity.AoiKey;
import com.huawei.imbp.etl.validation.DataTypeValidation;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 5/13/2019
 */

@Log4j2
public class ConversionData {

    static OnBuild onBuild;

    public static AoiEntity convert(Map<String, Object> payload) throws Exception{

        AoiEntity aoiEntity = new AoiEntity();
        AoiKey aoiKey = new AoiKey();
        aoiEntity.setKey(aoiKey);

        //primary keys
        aoiKey.setCreatedDay((String)payload.get("created_day"));
        aoiKey.setDeviceType((String)payload.get("device_type"));

        String createdTime = (String)payload.get("created_time");

        Long time = (Long)DataTypeValidation.checkDataType(DataType.TIMESTAMP, createdTime);
        Timestamp timestamp = new Timestamp(time);
        DateTime dateTime = new DateTime(timestamp.getTime());
        aoiKey.setHour(dateTime.getHourOfDay());
        aoiKey.setMinute(dateTime.getMinuteOfHour());
        aoiKey.setSecond(dateTime.getSecondOfMinute());

        aoiKey.setLabel((String)payload.get("label"));
        aoiKey.setCreatedTime(timestamp);

        //columns
        aoiEntity.setBoardId((String)payload.get("board_id"));
        aoiEntity.setBoardLoc((String)payload.get("board_loc"));
        aoiEntity.setExtension((String)payload.get("extension"));
        aoiEntity.setFileName((String)payload.get("file_name"));
        aoiEntity.setProductType((String)payload.get("product_type"));

        String imageStr = (String)payload.get("image");
        ByteBuffer image = (ByteBuffer)DataTypeValidation.checkDataType(DataType.BLOB, imageStr);
        aoiEntity.setImage(image);

        return aoiEntity;
    }


    public static Insert buildStatement(Map<String, Object> payload, String system) throws Exception{

        Insert insert = QueryBuilder.insertInto("images", "aoi_single_component_image");

        Object created_day = payload.get("created_day");
        insert.value("created_day", created_day);

        Object device_type = payload.get("device_type");
        insert.value("device_type", device_type);

        String createdTime = (String)payload.get("created_time");

        Long mills = (Long)DataTypeValidation.checkDataType(DataType.TIMESTAMP, createdTime);
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

            String primaryKey = device_type+"#"+hour +"#" + minute+"#"+second
                    + "#" + label + "#" + mills;
            try {
                redisTemplate.opsForZSet().add("secDate" + ":" + system + ":" + created_day,
                        primaryKey, mills).subscribe();
                redisTemplate.opsForZSet().add("secHour" + ":" + system + ":" + created_day + ":" + hour,
                        primaryKey, mills).subscribe();
                redisTemplate.opsForZSet().add("secDevice" + ":" + system + ":" + device_type,
                        primaryKey, mills).subscribe();
                redisTemplate.opsForZSet().add("secTime" + ":" + system + ":" + mills,
                        primaryKey, mills).subscribe();
                redisTemplate.opsForZSet().add("secDeviceTime" + ":" + system + ":" + mills + ":" + device_type,
                        primaryKey, mills).subscribe();
            }catch (Exception e){
                log.error(system + ":" + created_day+":"+primaryKey+"---"+e.getMessage());
            }
        });
        return insert;
    }

    public static void index(OnBuild onBuild){
        ConversionData.onBuild = onBuild;
    }

    public static void buildIndex(ReactiveRedisTemplate<String, String> redisTemplate){
        onBuild.onIndexBuild(redisTemplate);
    }
}
