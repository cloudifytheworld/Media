package com.huawei.mfg.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.huawei.mfg.util.CommonConstants;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfgResponse {
    private String status;
    private String error;
    private String message;

    public MfgResponse() {
        this.status = CommonConstants.Status.OK.type();
    }

    public MfgResponse(String status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MfgResponse{" +
                "status='" + status + '\'' +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
