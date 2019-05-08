package com.huawei.imbp.etl.transform;

import com.huawei.imbp.etl.common.DataType;
import lombok.extern.log4j.Log4j2;
import com.huawei.imbp.etl.service.DbService;

/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */

@Log4j2
public class BooleanConversion implements Conversion<Boolean> {

    @Override
    public Boolean convert(DataType sourceType, Object data, String targetFormat, String sourceFormat) throws Exception {

        log.debug("BooleanConversion from "+ DbService.dbDirection.SOURCE+" type "+sourceType.name()+" to "+ DbService.dbDirection.TARGET+" type BOOLEAN");

        switch (sourceType) {

            case TEXT:case VARCHAR:case BOOLEAN:
                return convertText(data.toString());
            case INT: case INTEGER:case LONG:
                return convertNumber(data.toString());
            default:
                throw imbpEx.setMessage("conversion from source "+sourceType.name()+ "to target BOOLEAN is not supported");

        }
    }

    public Boolean convertText(String data) throws Exception{

        try{
            return Boolean.parseBoolean(data);
        }catch (Exception e){
            throw imbpEx.setMessage("can't convert "+data+" to boolean");
        }
    }

    public Boolean convertNumber(String data) throws Exception{

        try{
            Long bool = Long.parseLong(data);
            if(bool == 1){
                return new Boolean(true);
            }else if(bool == 0){
                return new Boolean(false);
            }else {
                throw imbpEx;
            }
        }catch (Exception e){
            throw imbpEx.setMessage("can't convert "+data+" to boolean");
        }
    }
}
