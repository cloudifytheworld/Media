package com.fw.imbp.etl.transform;

import com.fw.imbp.etl.common.DataType;
import com.fw.imbp.etl.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

import com.fw.imbp.etl.service.DbService;


/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */

@Log4j2
public class DoubleConversion implements Conversion<Double>{

    @Override
    public Double convert(DataType sourceType, Object data, String targetFormat, String sourceFormat) throws Exception {

        log.debug("DoubleConversion from "+ DbService.dbDirection.SOURCE+" type "+sourceType.name()+" to "+ DbService.dbDirection.TARGET+" type DOUBLE");

        switch (sourceType) {
            case DOUBLE:case LONG:case TIMESTAMP:case FLOAT:case DECIMAL:case INTEGER:case TEXT:
                return convertByFormat(data.toString(), targetFormat);
            default:
                throw imbpEx.setMessage("conversion from "+sourceType.name()+ "to DOUBLE is not supported");
        }
    }


    public Double convertByFormat(String data, String targetFormat) throws Exception{

        Double value = DataUtil.checkValidDouble(data);
        if(value == null || value > Double.MAX_VALUE || value < Double.MIN_VALUE){
            throw imbpEx.setMessage("invalid conversion to double from source type");
        }

        if(!StringUtils.isEmpty(targetFormat)) {
            try {
                String formatted = DataUtil.convertNumberFormat(value, targetFormat);
                return Double.valueOf(formatted);
            } catch (Exception e) {
                throw imbpEx.setMessage("can't convert "+data+" to DOUBLE");
            }
        }else{
            return convertToDouble(data);
        }
    }

    public Double convertToDouble(String data) throws Exception{

        try{
            return Double.parseDouble(data);
        }catch (Exception e){
            throw imbpEx.setMessage("can't convert "+data+" to DOUBLE");
        }
    }
}
