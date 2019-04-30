package com.huawei.mfg.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MfgEtlServiceKey extends BaseBean implements Serializable {
    private String serviceKey;
    private String source;
    private boolean enabled;
    private String description;

    public MfgEtlServiceKey() {
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "MfgEtlServiceKey{" +
                "serviceKey='" + serviceKey + '\'' +
                ", source='" + source + '\'' +
                ", enabled=" + enabled +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }

}
