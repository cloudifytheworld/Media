package com.huawei.imbp.etl.util;

import com.huawei.imbp.etl.common.ImbpException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Map;

import static com.huawei.imbp.etl.common.ImbpCommon.*;
import static com.huawei.imbp.etl.common.ImbpCommon.BIGINT;

/**
 * @author Charles(Li) Cai
 * @date 4/10/2019
 */

@Log4j2
public class DataType {

    public static ImbpException imbpException = new ImbpException();

    public static Object buildDataType(Map<String, String> column, String dataType, Object value) throws Exception{

        switch (dataType.toLowerCase()){

            case TEXT: case VARCHAR:
                return value;
            case TIMESTAMP:
                try {
                    Long timestamp = DataUtil.checkValidDigital(value);
                    if(timestamp != null) {
                        if (timestamp <= 28800 || timestamp >= System.currentTimeMillis()) {
                            throw imbpException.setMessage("not right timestamp");
                        }
                    }else{
                        timestamp = DataUtil.checkColumnFormat(value, column);
                        if(timestamp == null){
                            throw imbpException.setMessage("not right timestamp, date must have defined format");
                        }
                    }
                    Timestamp date = new Timestamp(timestamp);
                    log.debug(date.toString());
                    return date;
                }catch (Exception e) {
                    throw e;
                }
            case BLOB:
                byte[] image = value.toString().getBytes();
                if(Base64.isBase64(image)) {
                    return ByteBuffer.wrap(Base64.decodeBase64(image));
                }
                return ByteBuffer.wrap(image);
            case BIGINT:
                Long data = DataUtil.checkValidDigital(value);
                if(data == null){
                    throw imbpException.setMessage("invalid long");
                }
            default:
                return value;
        }
    }
}
