package com.fw.imbp.etl.validation;

import com.fw.imbp.etl.common.ImbpException;
import com.fw.imbp.etl.service.DbService;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Charles(Li) Cai
 * @date 4/24/2019
 */

public class PatternValidation {

    public static final Map<String, Pattern> columnPattern = new HashMap<>();

    public static void checkColumnPattern(Object data, String dataPattern, DbService.dbDirection dir) throws Exception{

        Pattern pattern = columnPattern.get(dataPattern);
        if(pattern == null){
            pattern = Pattern.compile(dataPattern);
            columnPattern.put(dataPattern, pattern);
        }

        Matcher m = pattern.matcher(data.toString());
        if(!m.matches()){
            throw new ImbpException().setMessage(dir+"not match pattern");
        }
    }
}
