package com.fw.imbp.etl.transform;

import com.fw.imbp.etl.common.DataType;
import com.fw.imbp.etl.service.DbService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;

import java.nio.ByteBuffer;

/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */

@Log4j2
public class BlobConversion implements Conversion<Object>{

    @Override
    public Object convert(DataType sourceType, Object data, String targetFormat, String sourceFormat) throws Exception {

        log.debug("BlobConversion from "+ DbService.dbDirection.SOURCE+" type "+sourceType.name()+" to "+ DbService.dbDirection.TARGET+" type BLOB");

        switch (sourceType) {

            case BLOB:
                return convertByteBuffer(data.toString());
            case TEXT:case VARCHAR:
                return data.toString();
            default:
                throw imbpEx.setMessage("conversion from source "+sourceType.name()+ "to target BLOB is not supported");
        }
    }

    public ByteBuffer convertByteBuffer(String data){

        byte[] bytes = data.getBytes();
        if(Base64.isBase64(bytes)) {
            return ByteBuffer.wrap(Base64.decodeBase64(bytes));
        }
        return ByteBuffer.wrap(bytes);
    }
}
