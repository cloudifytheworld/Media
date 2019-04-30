package com.huawei.mfg.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.huawei.mfg.util.CommonConstants;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfgRequest implements Serializable {
    private String system; //AGV or other systems
    private String factory;
    private String equipment;
    private String table;
    private String sender;
    private String destination;
    private String payload;
    private CommonConstants.EtlProcessType etlProcessType;

    @JsonCreator
    public MfgRequest(@JsonProperty("system") String system,
                      @JsonProperty("factory") String factory,
                      @JsonProperty("equipment") String equipment,
                      @JsonProperty("table") String table,
                      @JsonProperty("sender") String sender,
                      @JsonProperty("target") String destination,
                      @JsonProperty("payload") String payload,
                      @JsonProperty("etlProcessType") CommonConstants.EtlProcessType etlProcessType)
    {
        this.system = system;
        this.factory = factory;
        this.equipment = equipment;
        this.table = table;
        this.sender = sender;
        this.destination = destination;
        this.payload = payload;
        this.etlProcessType = etlProcessType;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public CommonConstants.EtlProcessType getEtlProcessType() {
        return etlProcessType;
    }

    public void setEtlProcessType(CommonConstants.EtlProcessType etlProcessType) {
        this.etlProcessType = etlProcessType;
    }

    @Override
    public String toString() {
        return "MfgRequest{" +
                "system='" + system + '\'' +
                ", factory='" + factory + '\'' +
                ", equipment='" + equipment + '\'' +
                ", table='" + table + '\'' +
                ", sender='" + sender + '\'' +
                ", destination='" + destination + '\'' +
                ", payload='" + payload + '\'' +
                ", etlProcessType=" + etlProcessType +
                '}';
    }

}
