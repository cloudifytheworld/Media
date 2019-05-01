package com.huawei.imbp.etl.transform;

import com.huawei.imbp.etl.common.DataType;
import com.huawei.imbp.etl.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

import java.lang.*;
import com.huawei.imbp.etl.service.DbService;

/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */

@Log4j2
public class LongConversion implements Conversion<Long> {

    @Override
    public Long convert(DataType sourceType, Object data, String targetFormat, String sourceFormat) throws Exception{

        log.debug("LongConversion from "+ DbService.dbDirection.SOURCE+" type "+sourceType.name()+" to "+ DbService.dbDirection.TARGET+" type BIGINT");

        switch (sourceType){
            case TIMESTAMP: case LONG:
            case INT: case TEXT:
                return convertLong(data.toString(), targetFormat);
            default:
                throw imbpEx.setMessage("conversion from "+sourceType.name()+ "to BIGINT is not supported");
        }
    }


    public Long convertLong(String data, String targetFormat) throws Exception{

        Long value = DataUtil.checkValidLong(data);
        if(value == null || value > Long.MAX_VALUE || value < Long.MIN_VALUE){
            throw imbpEx.setMessage("invalid conversion to bigint from source type");
        }

        try{
            if(!StringUtils.isEmpty(targetFormat)) {
                String formatted = DataUtil.convertNumberFormat(value, targetFormat);
                return Long.parseLong(formatted);
            }else{
                return Long.parseLong(data);
            }
        }catch (Exception e){
            throw imbpEx.setMessage("can't parse to long for "+data+" "+e.getMessage());
        }
    }
}
