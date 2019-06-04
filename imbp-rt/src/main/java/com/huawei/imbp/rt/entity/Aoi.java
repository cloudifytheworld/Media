package com.huawei.imbp.rt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Charles(Li) Cai
 * @date 4/15/2019
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aoi implements Serializable {

    private String created_day;
    private String device_type;
    private String label;
    private int hour;
    private int minus;
    private int sec;
    private Date created_time;
    private String board_id;
    private String board_loc;
    private String extension;
    private String file_name;
    private String product_type;
    private String image;


}
