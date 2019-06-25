package com.fw.imbp.etl.transform;

import com.fw.imbp.etl.build.OnIndexBuild;
import com.fw.imbp.etl.common.DataType;
import com.fw.imbp.etl.entity.AoiEntity;
import com.fw.imbp.etl.entity.AoiKey;
import com.fw.imbp.etl.validation.DataTypeValidation;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 5/13/2019
 */

@Log4j2
public class ConversionData {

    static OnIndexBuild onIndexBuild;

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
}
