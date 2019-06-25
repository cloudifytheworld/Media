package com.fw.imbp.etl.transform;

import com.fw.imbp.etl.common.DataType;
import com.fw.imbp.etl.service.DbService;
import com.fw.imbp.etl.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;


/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */

@Log4j2
public class TextConversion implements Conversion<String>{

    @Override
    public String convert(DataType sourceType, Object data, String targetFormat, String sourceFormat) throws Exception{

        log.debug("TextConversion from "+DbService.dbDirection.SOURCE+" type "+sourceType.name()+" to "+
                DbService.dbDirection.TARGET+" type TEXT");

        switch (sourceType) {

            case DATE:case TEXT:
                return convertDateToText(data.toString(), targetFormat, sourceFormat);
            case TIMESTAMP:case LONG:case INTEGER:
                return convertNumberToTextFormat(data.toString(), targetFormat);
            default:
                return data.toString();
        }
    }

    public String convertDateToText(String data, String targetFormat, String sourceFormat) throws Exception{

        if(!StringUtils.isEmpty(targetFormat)){
            try {
                return DataUtil.convertDateToDate(data, targetFormat, sourceFormat);
            }catch (Exception e){
                throw imbpEx.setMessage("can't parse DATE with provided format "+e.getMessage());
            }
        }

        return data;
    }

    public String convertNumberToTextFormat(String data, String targetFormat) throws Exception{

        if(!StringUtils.isEmpty(data)){
            Long value = DataUtil.checkValidLong(data);
            if(value != null){
                try {
                   return DataUtil.convertNumberFormat(value, targetFormat);
                }catch (Exception e){
                    throw imbpEx.setMessage("can't parse NUMBER with provided format "+e.getMessage());
                }
            }
        }

        return data;
    }
}
