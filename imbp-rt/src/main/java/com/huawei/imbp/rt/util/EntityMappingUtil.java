package com.huawei.imbp.rt.util;

import com.datastax.driver.core.Row;
import com.huawei.imbp.rt.entity.Aoi;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * @author Charles(Li) Cai
 * @date 4/15/2019
 */

@Log4j2
public class EntityMappingUtil {

    public static Aoi mappingAoi(Row row){

        Aoi aoi = new Aoi();
        String created_day = row.getString("created_day");
        if(created_day != null) {
            aoi.setCreated_day(created_day);
        }

        String device_type = row.getString("device_type");
        if(device_type != null) {
            aoi.setDevice_type(device_type);
        }

        String label = row.getString("label");
        if(label != null) {
            aoi.setLabel(label);
        }

        Integer hour = row.getInt("hour");
        aoi.setHour(hour);

        Integer minus = row.getInt("mins");
        aoi.setMinus(minus);

        Long created_time = row.getTimestamp("created_time").getTime();
        if(created_time != null) {
            aoi.setCreated_time(new Date(created_time));
        }


        String board_id = row.getString("board_id");
        if(board_id != null) {
            aoi.setBoard_id(board_id);
        }

        String board_loc = row.getString("board_loc");
        if(board_loc !=null) {
            aoi.setBoard_loc(board_loc);
        }

        String extension = row.getString("extension");
        if(extension !=null) {
            aoi.setExtension(extension);
        }

        String file_name = row.getString("file_name");
        if(file_name != null) {
            aoi.setFile_name(file_name);
        }

        ByteBuffer image = row.getBytes("image");

        if(image != null) {
            try{
                //String deserialize = TypeCodec.varchar().deserialize(image, ProtocolVersion.V5);
                String img = Base64.encodeBase64String(image.array());
                aoi.setImage(img);
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }

        String product_type = row.getString("product_type");
        if(product_type != null) {
            aoi.setProduct_type(product_type);
        }

        return aoi;

    }
}
