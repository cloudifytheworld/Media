package com.huawei.imbp.etl.entity;


import java.io.Serializable;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 4/8/2019
 */

public class ActionEntity implements Serializable {

    String id;
    Map<String, Object> input;
    String errorMsg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
