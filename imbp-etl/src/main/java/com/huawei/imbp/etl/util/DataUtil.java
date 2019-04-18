package com.huawei.imbp.etl.util;


import com.google.common.base.Throwables;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huawei.imbp.etl.common.ImbpException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author Charles(Li) Cai
 * @date 3/7/2019
 */

@Log4j2
public class DataUtil {

    public static Map<String, Pattern> columnPattern = new HashMap<>();
    public static Map<String, DateTimeFormatter> columnFormat = new HashMap<>();
    public static ImbpException imbpException = new ImbpException();

    public static Long checkValidDigital(Object value){

        try{
            return Long.parseLong(value.toString());
        }catch (Exception e){
            return null;
        }

    }

    public static void checkInputData(Object data, Map<String, String> column) throws Exception{

        checkColumnNullable(data, column);
        checkColumnPattern(data, column);
        checkColumnFormat(data, column);
    }

    public static void checkColumnNullable(Object data, Map<String, String> column) throws Exception{

        Object nullable = column.get("nullable");
        if (nullable != null && !(Boolean) nullable) {
            if(StringUtils.isEmpty(data.toString().trim())){
                throw imbpException.setMessage("can't be empty");
            }
        }
    }


    public static void checkColumnPattern(Object data, Map<String, String> column) throws Exception{

        String dataPattern = column.get("columnPattern");
        if(StringUtils.isEmpty(dataPattern)) return;

        Pattern pattern = columnPattern.get(dataPattern);
        if(pattern == null){
            pattern = Pattern.compile(dataPattern);
            columnPattern.put(dataPattern, pattern);
        }

        Matcher m = pattern.matcher(data.toString());
        if(!m.matches()){
         throw imbpException.setMessage("not match pattern");
        }
    }

    public static Long checkColumnFormat(Object data, Map<String, String> column) throws Exception{

        String dataFormat = column.get("columnFormat");
        if(StringUtils.isEmpty(dataFormat)) return null;

        DateTimeFormatter dtf = columnFormat.get(dataFormat);
        if(dtf == null){
            dtf = DateTimeFormat.forPattern(dataFormat);
            columnFormat.put(dataFormat, dtf);
        }

        try {
            DateTime dateTime = dtf.parseDateTime(data.toString());
            return dateTime.getMillis();
        }catch (Exception e){
            throw imbpException.setMessage("not match format");
        }
    }

    public static Set<String> getPrimaryKey(String primaryKeys){

        String[] keys = primaryKeys.split(",");
        return Arrays.stream(keys).map(s -> s.trim())
                .collect(Collectors.toSet());
    }

    public static String showImageSize(Map<String, Object> input, final String debugImage) {

        String json = new Gson().toJson(input);
        Map<String, Object> tmp = new Gson().fromJson(
                json, new TypeToken<Map<String, Object>>() {}.getType());

        Object payload = tmp.get("payload");
        try {
            if (List.class.isInstance(payload)) {
                List<Map<String, String>> images = List.class.cast(payload);
                images.stream().forEach(content -> {
                    try {
                        setImageLength(content, debugImage);
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                });
            } else {
                Map<String, String> payloadMap = Map.class.cast(payload);
                    setImageLength(payloadMap, debugImage);
            }
        }catch (Exception e){
            log.debug(Throwables.getStackTraceAsString(e));
            throw e;
        }
        return tmp.toString();
    }

    private static void setImageLength(Map<String, String> imageMap, final String debugImage) {

        String image = imageMap.get(debugImage);
        if(StringUtils.isEmpty(image)) {
            throw new RuntimeException("image not map for debug");
        }
        imageMap.put(debugImage, image.length()+"");
    }

    public static int roundupToNextInt(float num){
        return (num == 0.0f)?(int)num:(int)num+1;
    }

    public static long getOffset(Long data){
        return (data >> 32) + ((data & Long.decode("0xFFFFFFFF")) >> 1);
    }

    public static String createId(Map<String, Object> input){

        HashFunction hashing = Hashing.murmur3_128();
        String id = hashing.hashUnencodedChars(input.toString()).toString();
        return id;
    }
}
