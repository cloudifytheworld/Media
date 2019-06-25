package com.fw.imbp.rt.entity;

import lombok.Data;

import java.util.Set;

/**
 * @author Charles(Li) Cai
 * @date 5/3/2019
 */

@Data
public class DateDevice {

    Set<String> indexes;
    Set<String> deviceTypes;
    String date;
    int hour;

}
