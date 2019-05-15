package com.huawei.imbp.rt.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.net.InetAddresses;
import com.huawei.imbp.rt.common.ImbpException;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Charles(Li) Cai
 * @date 5/14/2019
 */
public class DataUtil {

    private static ImbpException imbpException = new ImbpException();

    public static boolean isNumber(String num){

        Integer dt = checkValidInteger(num);
        if(dt == null || dt > Integer.MAX_VALUE || dt < Integer.MIN_VALUE){
            return false;
        }
        return true;

    }



    public static Long checkValidLong(Object value){

        try{
            return Long.parseLong(value.toString());
        }catch (Exception e){
            return null;
        }

    }

    public static Integer checkValidInteger(Object value){

        try{
            return Integer.parseInt(value.toString());
        }catch (Exception e){
            return null;
        }

    }
    public static Boolean checkValidBoolean(Object value){

        try{
            return Boolean.parseBoolean(value.toString());
        }catch (Exception e){
            return null;
        }

    }
    public static Double checkValidDouble(Object value){

        try{
            return Double.parseDouble(value.toString());
        }catch (Exception e){
            return null;
        }
    }

    public static boolean checkIpAddress(Object value){

        return InetAddresses.isInetAddress(value.toString());
    }

    public static UUID checkValidUUID(Object value){

        try{
            return UUID.fromString(value.toString());
        }catch (Exception e){
            return null;
        }
    }



}
