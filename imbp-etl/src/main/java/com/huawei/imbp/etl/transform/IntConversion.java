package com.huawei.imbp.etl.transform;

import com.huawei.imbp.etl.common.DataType;
import com.huawei.imbp.etl.util.DataUtil;
import com.huawei.imbp.etl.service.DbService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */

@Log4j2
public class IntConversion implements Conversion<Integer>{

    @Override
    public Integer convert(DataType sourceType, Object data, String targetFormat, String sourceFormat) throws Exception {

        log.debug("IntConversion from "+ DbService.dbDirection.SOURCE+" type "+sourceType.name()+" to "+ DbService.dbDirection.TARGET+" type Int");

        switch (sourceType) {
            case INTEGER:case TEXT:case LONG:case TIMESTAMP:case DECIMAL:case FLOAT:
                convertByFormat(data.toString(), targetFormat);
            default:
                throw imbpEx.setMessage("conversion from "+sourceType.name()+ "to INT is not supported");
        }

    }

    public Integer convertByFormat(String data, String targetFormat) throws Exception{

        Integer value = DataUtil.checkValidInteger(data);
        if(value == null || value > Integer.MAX_VALUE || value < Integer.MIN_VALUE){
            throw imbpEx.setMessage("invalid conversion to int from source type");
        }

        try{
            if(!StringUtils.isEmpty(targetFormat)) {
                String formatted = DataUtil.convertNumberFormat(value, targetFormat);
                return Integer.parseInt(formatted);
            }else{
                return Integer.parseInt(data);
            }
        }catch (Exception e){
            throw imbpEx.setMessage("can't parse to int for "+data+" "+e.getMessage());
        }
    }

}
