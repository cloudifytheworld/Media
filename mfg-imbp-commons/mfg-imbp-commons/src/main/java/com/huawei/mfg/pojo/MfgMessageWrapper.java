package com.huawei.mfg.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.huawei.mfg.util.CommonConstants;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfgMessageWrapper implements Serializable {
    private String topic;
    private String table;
    private CommonConstants.Target target;
    private String payload;
    private List<Map<String, Object>> entries;
    private CommonConstants.EtlProcessType etlProcessType;

    public MfgMessageWrapper(
            String topic,
            String payload,
            CommonConstants.Target target,
            CommonConstants.EtlProcessType etlProcessType)
    {
        this.topic = topic;
        this.payload = payload;
        this.target = target;
        this.etlProcessType = etlProcessType;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public CommonConstants.Target getTarget() {
        return target;
    }

    public void setTarget(CommonConstants.Target target) {
        this.target = target;
    }

    public List<Map<String, Object>> getEntries() {
        return entries;
    }

    public void setEntries(List<Map<String, Object>> entries) {
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
        return "MfgMessageWrapper{" +
                "topic='" + topic + '\'' +
                ", table='" + table + '\'' +
                ", target=" + target +
                ", payload='" + payload + '\'' +
                ", entries=" + entries +
                ", etlProcessType=" + etlProcessType +
                '}';
    }
}
