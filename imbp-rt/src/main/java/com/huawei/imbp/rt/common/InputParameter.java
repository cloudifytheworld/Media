package com.huawei.imbp.rt.common;

import lombok.Data;
import org.omg.CORBA.ServerRequest;

import java.util.Date;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 5/15/2019
 */
@Data
public class InputParameter {

    private String system;
    private String deviceType;
    private String[] from;
    private String to;
    private Integer hour;
    private Integer minute;
    private String label;
    private Date createdTime;

}
