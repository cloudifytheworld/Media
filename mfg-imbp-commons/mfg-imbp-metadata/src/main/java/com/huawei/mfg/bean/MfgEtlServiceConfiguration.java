package com.huawei.mfg.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MfgEtlServiceConfiguration extends BaseBean implements Serializable {
    private int keyId;
    private String serviceValue;
    private String description;

    public MfgEtlServiceConfiguration() {
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public String getServiceValue() {
        return serviceValue;
    }

    public void setServiceValue(String serviceValue) {
        this.serviceValue = serviceValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "MfgEtlServiceConfiguration{" +
                "keyId=" + keyId +
                ", serviceValue='" + serviceValue + '\'' +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }

}
