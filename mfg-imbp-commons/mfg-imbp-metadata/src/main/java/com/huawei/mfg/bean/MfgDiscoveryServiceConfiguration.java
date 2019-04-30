package com.huawei.mfg.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MfgDiscoveryServiceConfiguration extends BaseBean implements Serializable {
    private String host;
    private int port;
    private String description;

    public MfgDiscoveryServiceConfiguration() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "MfgDiscoveryServiceConfiguration{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }

}
