package com.fw.imbp.rt.util;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.fw.imbp.rt.common.ImbpException;
import com.fw.imbp.rt.entity.ClientData;
import com.fw.imbp.rt.entity.ClientDateTime;
import org.joda.time.DateTime;

import org.joda.time.DateTimeComparator;
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


    public static Integer checkValidInteger(Object value){

        try{
            return Integer.parseInt(value.toString());
        }catch (Exception e){
            return null;
        }

    }

    public static List<ClientDateTime> getDateTimes(DateTime startTime, DateTime endTime) throws Exception{

        List<ClientDateTime> dateTimes = new ArrayList<>();
        DateTime nextTime = startTime;

        try {
            if (endTime.getDayOfMonth() - startTime.getDayOfMonth() > 0) {

                ClientDateTime clientDateTime = new ClientDateTime();
                nextTime = endOfDateTime(nextTime);
                clientDateTime.setDate(toDateString(startTime));
                clientDateTime.setStartTime(startTime.getMillis());
                clientDateTime.setEndTime(nextTime.getMillis());
                nextTime = nextTime.plusMillis(1);
                dateTimes.add(clientDateTime);
            }

            while(DateTimeComparator.getInstance().compare(nextTime, endTime) <= 0){
                ClientDateTime clientDateTime = new ClientDateTime();
                clientDateTime.setDate(toDateString(nextTime));
                clientDateTime.setStartTime(nextTime.getMillis());
                nextTime = nextTime.plusDays(1);
                if(DateTimeComparator.getInstance().compare(nextTime, endTime) <= 0) {
                    clientDateTime.setEndTime(nextTime.getMillis()-1);
                }else{
                    clientDateTime.setEndTime(endTime.getMillis());
                }
                dateTimes.add(clientDateTime);
            }
        }catch (Exception e){
            throw e;
        }

        return dateTimes;
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

    public static String toDateString(DateTime dateTime){
        return dateTime.toString(dateFormat);
    }

}
