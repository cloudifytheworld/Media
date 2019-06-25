package com.fw.imbp.etl.validation;

import com.fw.imbp.etl.common.ImbpException;
import com.fw.imbp.etl.service.DbService;
import org.apache.commons.lang.StringUtils;


/**
 * @author Charles(Li) Cai
 * @date 4/24/2019
 */

public class NullableValidation {

    public static void checkColumnNullable(Object data, Object nullable, DbService.dbDirection dir) throws Exception{

        try {
            if (!(Boolean) nullable) {
                if (StringUtils.isEmpty(data.toString().trim())) {
                    throw new ImbpException().setMessage(dir.name() + "can't be empty");
                }
            }
        }catch (Exception e){
            throw new ImbpException().setMessage(nullable+" is not boolean value");
        }
    }
}
