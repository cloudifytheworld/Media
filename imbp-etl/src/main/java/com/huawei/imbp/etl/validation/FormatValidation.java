package com.huawei.imbp.etl.validation;

import com.huawei.imbp.etl.common.DataType;
import com.huawei.imbp.etl.common.ImbpException;
import com.huawei.imbp.etl.service.DbService;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 4/24/2019
 */
public class FormatValidation {

    public static final Map<String, DateTimeFormatter> columnFormat = new HashMap<>();

    public static void checkColumnFormat(Object data, String dataFormat, DbService.dbDirection dir, DataType dataType) throws Exception{

            switch (dataType){

                case TIMESTAMP: case DATE: case TEXT:
                    validateDate(data, dataFormat, dir); break;
                case DECIMAL:case LONG:case INTEGER:
                case DOUBLE:case FLOAT:
                    validateNumber(data, dataFormat, dir);
                default:
                    throw new ImbpException().setMessage("format is not supported on the dataType "+dataType.name());
            }
    }

    public static void validateDate(Object data, String dataFormat, DbService.dbDirection dir) throws Exception{

        DateTimeFormatter dtf = columnFormat.get(dataFormat);
        if(dtf == null){
            dtf = DateTimeFormat.forPattern(dataFormat);
            columnFormat.put(dataFormat, dtf);
        }

        try {
            dtf.parseDateTime(data.toString());
        }catch (Exception e){
            throw new ImbpException().setMessage(dir+" not match format");
        }
    }

    public static void validateNumber(Object data, String numberFormat, DbService.dbDirection dir) throws Exception{

        NumberFormat format = new DecimalFormat(numberFormat);
        String result = format.format(data);
        if(!result.equals(data)){
            throw new ImbpException().setMessage(dir+" not match format");
        }
    }
}
