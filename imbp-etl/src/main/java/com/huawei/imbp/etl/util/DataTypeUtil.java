package com.huawei.imbp.etl.util;


import java.nio.ByteBuffer;
import java.util.Date;

import static com.huawei.imbp.etl.common.ImbpCommon.BLOB;
import static com.huawei.imbp.etl.common.ImbpCommon.TEXT;
import static com.huawei.imbp.etl.common.ImbpCommon.TIMESTAMP;
import static com.huawei.imbp.etl.common.ImbpCommon.VARCHAR;


public class DataTypeUtil {


    public static Object buildCassandraDataType(String dataType, String value){

        switch (dataType){

            case TEXT: case VARCHAR:
                return value;
            case TIMESTAMP:
                return new Date(Long.parseLong(value));
            case BLOB:
                return  ByteBuffer.wrap(value.getBytes());
            default:
                return value;
        }
    }
}
