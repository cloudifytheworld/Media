package com.huawei.imbp.rt.util;


import com.google.common.net.InetAddresses;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.huawei.imbp.rt.common.ImbpException;
import com.huawei.imbp.rt.transfer.ClientData;
import org.joda.time.DateTime;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;


/**
 * @author Charles(Li) Cai
 * @date 5/14/2019
 */
public class DataUtil {

    private static final ImbpException imbpException = new ImbpException();
    private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyyMMdd");
    private static final DateTimeFormatter timeFormat = DateTimeFormat.forPattern("yyyyMMddHHmmss");

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

    public static String[] convertStringToArray(String line){
        String[] data = Arrays.stream(line.split(",")).map(String::trim).toArray(String[]::new);
        return data;
    }

    public static ClientData convertMapToObject(Map map, Class<ClientData> clazz){

        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(map);
        ClientData clientData = gson.fromJson(jsonElement, clazz);
        return clientData;
    }

    public static DateTime convertDate(String date) throws Exception{

        try{
            DateTime dateTime = dateFormat.parseDateTime(date);
            return dateTime;
        }catch (Exception e){
            throw imbpException.setMessage("can't convert date");
        }
    }

    public static DateTime convertDateTime(String time) throws Exception{

        try{
            DateTime dateTime = timeFormat.parseDateTime(time);
            return dateTime;
        }catch (Exception e){
            throw imbpException.setMessage("can't convert date");
        }
    }

    public static DateTime endOfDateTime(DateTime startTime) throws Exception{

        try{
            String date = startTime.getYear()+""+startTime.getMonthOfYear()+""+startTime.getDayOfMonth();
            DateTime dateTime = convertDate(date).plusDays(1).minusMillis(1);
            return dateTime;
        }catch (Exception e){
            throw imbpException.setMessage("can't get end of date for "+startTime.toString());
        }

    }

}
