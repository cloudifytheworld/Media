package com.fw.imbp.etl.validation;

import com.fw.imbp.etl.common.DataType;
import com.fw.imbp.etl.common.ImbpException;
import com.fw.imbp.etl.util.DataUtil;

import org.apache.commons.codec.binary.Base64;
import java.nio.ByteBuffer;


/**
 * @author Charles(Li) Cai
 * @date 4/24/2019
 */

public class DataTypeValidation {

    static ImbpException imbpException = new ImbpException();

    public static Object checkDataType(DataType dataType, Object value) throws Exception{

        switch (dataType){

            case TEXT: case VARCHAR: case DATE:case DATETIME:
                return value;
            case TIMESTAMP:
                    Long timestamp = DataUtil.checkValidLong(value);
                    if(timestamp != null) {
                        if (timestamp < 28800000 || timestamp >= System.currentTimeMillis()) {
                            throw imbpException.setMessage("not right timestamp");
                        }
                    }
                    return timestamp;
            case BLOB:
                byte[] image = value.toString().getBytes();
                if(Base64.isBase64(image)) {
                    return ByteBuffer.wrap(Base64.decodeBase64(image));
                }
                return ByteBuffer.wrap(image);
            case BIGINT:
                Long data = DataUtil.checkValidLong(value);
                if(data == null || data > Long.MAX_VALUE || data < Long.MIN_VALUE){
                    throw imbpException.setMessage("invalid long");
                }
                return data;
            case INT: case COUNTER: case VARINT: case INTEGER:
                Integer dt = DataUtil.checkValidInteger(value);
                if(dt == null || dt > Integer.MAX_VALUE || dt < Integer.MIN_VALUE){
                    throw imbpException.setMessage("invalid Integer");
                }
                return dt;
            case BOOLEAN:
                Boolean b = DataUtil.checkValidBoolean(value);
                if(b == null) {
                    throw imbpException.setMessage("invalid boolean");
                }
                return b;
            case DECIMAL: case FLOAT:
                Double ft = DataUtil.checkValidDouble(value);
                if(ft == null || ft > Float.MAX_VALUE || ft < Float.MIN_VALUE) {
                    throw imbpException.setMessage("invalid decimal float");
                }
                return ft;
            case DOUBLE:
                Double db = DataUtil.checkValidDouble(value);
                if(db == null || db > Double.MAX_VALUE || db < Double.MIN_VALUE) {
                    throw imbpException.setMessage("invalid double");
                }
                return db;
            case INET:
                if(!DataUtil.checkIpAddress(value)){
                    throw imbpException.setMessage("not right ip address format");
                }
                return value;
            case UUID: case TIMEUUID:
                java.util.UUID uuid = DataUtil.checkValidUUID(value);
                if(uuid == null){
                    throw imbpException.setMessage("invalid uuid");
                }
                return uuid;
            case TINYINT:
                Integer in = DataUtil.checkValidInteger(value);
                if(in == null || in > 127 || in < -128){
                    throw imbpException.setMessage("invalid tiny int");
                }
                return in;
            case SMALLINIT:
                Integer sn = DataUtil.checkValidInteger(value);
                if(sn == null || sn > 32767 || sn < -32768){
                    throw imbpException.setMessage("invalid small int");
                }
                return sn;
            default:
                throw imbpException.setMessage("dataType not mapped");
        }
    }
}
