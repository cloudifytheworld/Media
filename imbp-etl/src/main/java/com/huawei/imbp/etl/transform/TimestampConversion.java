package com.huawei.imbp.etl.transform;

import com.huawei.imbp.etl.common.DataType;
import com.huawei.imbp.etl.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import com.huawei.imbp.etl.service.DbService;

import java.sql.Timestamp;


/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */

@Log4j2
public class TimestampConversion implements Conversion<Object> {

    @Override
    public Object convert(DataType sourceType, Object data, String targetFormat, String sourceFormat) throws Exception {

        log.debug("TimestampConversion from "+ DbService.dbDirection.SOURCE+" type "+sourceType.name()+" to "+ DbService.dbDirection.TARGET+" type TIMESTAMP");

        switch (sourceType) {

            case INT:case INTEGER:case LONG:case TIMESTAMP:
                return convertNumberToTimestamp(data.toString(), targetFormat);
            case DATE:
                return convertDateToDateFormat(data.toString(), targetFormat, sourceFormat);
            default:
                throw imbpEx.setMessage("conversion from "+sourceType.name()+ "to TIMESTAMP is not supported");

        }
    }

    public Object convertNumberToTimestamp(String data, String targetFormat) throws Exception{

        Long timestamp = DataUtil.checkValidLong(data);
        if(timestamp == null) throw imbpEx.setMessage(data+" is not long to be converted to timestamp");

        if(StringUtils.isEmpty(targetFormat)) {
            if (timestamp < 28800000) {
                throw imbpEx.setMessage("is smaller than Jan 1, 1970");
            }

            if(timestamp > System.currentTimeMillis()){
                throw imbpEx.setMessage("is bigger than current local server time");
            }
            return new Timestamp(timestamp);
        }

        return DataUtil.convertNumberToDate(timestamp, targetFormat);
    }

    public String convertDateToDateFormat(String data, String targetFormat, String sourceFormat) throws Exception{

        if(!StringUtils.isEmpty(targetFormat)) {
            try {
                Long dateLong = DataUtil.checkValidLong(data);
                if(dateLong == null){
                    return DataUtil.convertNumberToDate(dateLong, targetFormat);
                }
                return DataUtil.convertDateToDate(data, targetFormat, sourceFormat);
            }catch (Exception e){
                throw imbpEx.setMessage("can't convert "+data+" to "+targetFormat+" "+e.getMessage());
            }
        }

        return data;
    }
}
