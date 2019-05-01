package com.huawei.imbp.etl.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author Charles(Li) Cai
 * @date 4/29/2019
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultEntity {

    String status;
    String message;
    List<String> ids;
    String id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
