package com.huawei.mfg.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.huawei.mfg.util.CommonConstants;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MfgCollectionWrapper<T> {
    private CommonConstants.RedisDataType redisDataType;
    private String key;
    private String sender;
    private String tableName;
    private String targetName;
    private CommonConstants.EtlProcessType etlProcessType;
    private T collection;

    @JsonCreator
    public MfgCollectionWrapper(
            @JsonProperty("redisDataType") CommonConstants.RedisDataType redisDataType,
            @JsonProperty("key") String key,
            @JsonProperty("collection") T collection,
            @JsonProperty("sender") String sender,
            @JsonProperty("tableName") String tableName,
            @JsonProperty("targetName") String targetName,
            @JsonProperty("etlProcessType") CommonConstants.EtlProcessType etlProcessType)
    {
        this.redisDataType = redisDataType;
        this.key = key;
        this.collection = collection;
        this.sender = sender;
        this.tableName = tableName;
        this.targetName = targetName;
        this.etlProcessType = etlProcessType;
    }

    public CommonConstants.RedisDataType getRedisDataType() {
        return redisDataType;
    }

    public void setRedisDataType(CommonConstants.RedisDataType redisDataType) {
        this.redisDataType = redisDataType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getCollection() {
        return collection;
    }

    public void setCollection(T collection) {
        this.collection = collection;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public CommonConstants.EtlProcessType getEtlProcessType() {
        return etlProcessType;
    }

    public void setEtlProcessType(CommonConstants.EtlProcessType etlProcessType) {
        this.etlProcessType = etlProcessType;
    }

    @Override
    public String toString() {
        return "MfgCollectionWrapper{" +
                "redisDataType=" + redisDataType +
                ", key='" + key + '\'' +
                ", sender='" + sender + '\'' +
                ", tableName='" + tableName + '\'' +
                ", targetName='" + targetName + '\'' +
                ", etlProcessType=" + etlProcessType +
                ", collection=" + collection +
                '}';
    }

}
