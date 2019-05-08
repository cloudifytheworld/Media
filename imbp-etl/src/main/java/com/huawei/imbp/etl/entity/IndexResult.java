package com.huawei.imbp.etl.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Charles(Li) Cai
 * @date 4/16/2019
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndexResult {

    private String id;
    private String errorMsg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
