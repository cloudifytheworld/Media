package com.huawei.imbp.imbprt.entity;

import java.util.Set;

/**
 * @author Charles(Li) Cai
 * @date 5/3/2019
 */

public class DateDevice {

    Set<String> deviceTypes;
    String date;
    int hour;

    public Set<String> getDeviceTypes() {
        return deviceTypes;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setDeviceTypes(Set<String> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
