package com.huawei.imbp.rt.util;

import java.text.DecimalFormat;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Charles(Li) Cai
 * @date 4/23/2019
 */

public class StatisticManager {

    private static DecimalFormat df2 = new DecimalFormat("#.###");

    public static SortedMap<String, SortedMap<String, Double>> statistics = new TreeMap<>();
    public static SortedMap<String, Double> deviceSize = new TreeMap<>();
    public static SortedMap<String, Double> eachHourSize = new TreeMap<>();
    public static SortedMap<String, Double> eachMinusSize = new TreeMap<>();
    public static SortedMap<String, Long> eachDay = new TreeMap<>();

    public static volatile double total = 0;
    public static volatile long counter = 0;


    public static void init(){

        statistics = new TreeMap<>();
        deviceSize = new TreeMap<>();
        eachHourSize = new TreeMap<>();
        eachMinusSize = new TreeMap<>();
        eachDay = new TreeMap<>();
        total = 0;
    }


    public static void putDay(String date, Long take){

        Long e = eachDay.get(date) == null?0L:eachDay.get(date);
        eachDay.put(date, e+take);
    }

    public static void putDevice(String key, long size) {

        Double len = deviceSize.get(key+" (M)");
        if (len == null) {
            deviceSize.put(key+" (M)", Double.valueOf(df2.format((double) size / 1000000)));
        } else {
            Double length = (double) size / 1000000;
            deviceSize.put(key+" (M)", Double.valueOf(df2.format(length + len)));
        }
    }


    public static void putEachMinus(String key, long size){

        Double len = eachMinusSize.get(key+" (M)");
        if(len == null){
            eachMinusSize.put(key+" (M)", Double.valueOf(df2.format((double)size/1000000)));
        }else {
            Double length = (double) size / 1000000;
            eachMinusSize.put(key+" (M)", Double.valueOf(df2.format(length + len)));
        }

    }


    public static void putEachHour(String key, long size){

        Double len = eachHourSize.get(key+" (M)");
        if(len == null){
            eachHourSize.put(key+" (M)", Double.valueOf(df2.format((double)size/1000000)));
        }else {
            Double length = (double) size / 1000000;
            eachHourSize.put(key+" (M)", Double.valueOf(df2.format(length + len)));        }

    }

    public static void put(String key, int size){

        SortedMap<String, Double> value = statistics.get(key);
        if(value == null){
            value = new TreeMap<>();
            value.put("size", Double.valueOf(df2.format((double)size)));
        }else{
            value.put("size", Double.valueOf(df2.format(value.get("size")+size)));
        }
        statistics.put(key, value);
    }

    public static void putBytes(String key, long len){

        SortedMap<String, Double> value = statistics.get(key);
        if(value == null){
            value = new TreeMap();
            value.put("bytes(M)", Double.valueOf(df2.format((double)len/1000000)));
        }else{
            Double size = value.get("bytes(M)");
            if(size == null) {
                value.put("bytes(M)", Double.valueOf(df2.format((double)len / 1000000)));
            }else{

                value.put("bytes(M)", Double.valueOf(df2.format(size+(double)len / 1000000)));
            }
        }
        statistics.put(key, value);

    }

    public static void putMinus(String key, long len){

        SortedMap<String, Double> value = statistics.get(key);
        if(value == null){
            value = new TreeMap();
            value.put("minus(K)", Double.valueOf(df2.format((double)len/1000)));
            value.put("minSize", 1d);
        }else{
            Double size = value.get("minus(K)");
            Double num = value.get("minSize");
            if(size == null) {
                value.put("minus(K)", Double.valueOf(df2.format((double)len / 1000)));
                value.put("minSize", 1d);
            }else {
                value.put("minus(K)", Double.valueOf(df2.format(size+(double)len / 1000)));
                value.put("minSize", num+1d);
            }
        }
        statistics.put(key, value);

    }
}
