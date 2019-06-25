package com.fw.imbp.etl.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 4/8/2019
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionEntity implements Serializable {

    private String id;
    private String system;
    private Map<String, Object> input;
    private String errorMsg;
}
