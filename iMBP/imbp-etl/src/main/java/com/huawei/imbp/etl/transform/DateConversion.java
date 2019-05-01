package com.huawei.imbp.etl.transform;

import com.datastax.driver.core.LocalDate;
import com.huawei.imbp.etl.common.DataType;
import com.huawei.imbp.etl.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import com.huawei.imbp.etl.service.DbService;

/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */

@Log4j2
public class DateConversion implements Conversion<Object> {

    @Override
    public Object convert(DataType sourceType, Object data, String targetFormat, String sourceFormat) throws Exception {

        log.debug("DateConversion from "+ DbService.dbDirection.SOURCE+" type "+sourceType.name()+" to "+ DbService.dbDirection.TARGET+" type DATE");

        switch (sourceType) {

            case LONG:case TIMESTAMP:case INT:case INTEGER:
                return convertNumberToDate(data.toString(), targetFormat);
            case DATE:
                return convertDateToDate(data.toString(), targetFormat, sourceFormat);
            default:
                throw imbpEx.setMessage("conversion from source "+sourceType.name()+ "to target DATE is not supported");
        }
    }

    public Object convertNumberToDate(String data, String targetFormat) throws Exception{

        try{
            Long time = DataUtil.checkValidLong(data);
            if(time == null) throw imbpEx.setMessage(data+" is not number to convert to date");

            if(StringUtils.isEmpty(targetFormat)) {
                return LocalDate.fromMillisSinceEpoch(time);
            }else{
                return DataUtil.convertNumberToDate(time, targetFormat);
            }
        }catch (Exception e){
            throw imbpEx.setMessage(e.getMessage());
        }
    }

    public String convertDateToDate(String data, String targetFormat, String sourceFormat) throws Exception{

        if(!StringUtils.isEmpty(targetFormat)) {
            try {
                return DataUtil.convertDateToDate(data, targetFormat, sourceFormat);
            } catch (Exception e) {
                throw imbpEx.setMessage("can't convert date to date");
            }
        }

        return data;
    }
}
