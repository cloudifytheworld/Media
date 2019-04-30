package com.huawei.mfg.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.huawei.mfg.util.CommonConstants;

import java.io.Serializable;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfgInternalMessage implements Serializable {
    private String system;
    private String factory;
    private String equipment;
    private String sender;
    private String table;
    private CommonConstants.Target target;
    private Map<String, Object> entries;
    private CommonConstants.EtlProcessType etlProcessType;

    public MfgInternalMessage(
            String system,
            String factory,
            String equipment,
            String table,
            CommonConstants.Target target,
            Map<String, Object> entries,
            CommonConstants.EtlProcessType etlProcessType)
    {
        this.system = system;
        this.factory = factory;
        this.equipment = equipment;
        this.table = table;
        this.target = target;
        this.entries = entries;
        this.etlProcessType = etlProcessType;
    }

    @JsonCreator
    public MfgInternalMessage(
            @JsonProperty("system") String system,
            @JsonProperty("factory") String factory,
            @JsonProperty("equipment") String equipment,
            @JsonProperty("table") String table,
            @JsonProperty("sender") String sender,
            @JsonProperty("target") CommonConstants.Target target,
            @JsonProperty("entries") Map<String, Object> entries,
            @JsonProperty("etlProcessType") CommonConstants.EtlProcessType etlProcessType)
    {
        this.system = system;
        this.factory = factory;
        this.equipment = equipment;
        this.table = table;
        this.sender = sender;
        this.target = target;
        this.entries = entries;
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public CommonConstants.Target getTarget() {
        return target;
    }

    public void setTarget(CommonConstants.Target target) {
        this.target = target;
    }

    public Map<String, Object> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, Object> entries) {
        this.entries = entries;
    }

    public CommonConstants.EtlProcessType getEtlProcessType() {
        return etlProcessType;
    }

    public void setEtlProcessType(CommonConstants.EtlProcessType etlProcessType) {
        this.etlProcessType = etlProcessType;
    }

    @Override
    public String toString() {
        return "MfgInternalMessage{" +
                "system='" + system + '\'' +
                ", factory='" + factory + '\'' +
                ", equipment='" + equipment + '\'' +
                ", sender='" + sender + '\'' +
                ", table='" + table + '\'' +
                ", target=" + target +
                ", entries=" + entries +
                ", etlProcessType=" + etlProcessType +
                '}';
    }
}
