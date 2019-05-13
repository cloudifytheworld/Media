package com.huawei.imbp.etl.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Charles(Li) Cai
 * @date 4/8/2019
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndexEntity {

    Long index;
    String errorMsg;

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
