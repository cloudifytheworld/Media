package com.huawei.mfg.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.huawei.mfg.pojo.MfgInternalMessage;
import com.huawei.mfg.util.CommonConstants;
import oracle.net.aso.e;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

final public class MfgRestMessageUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MfgRestMessageUtils() {}

    public static MfgInternalMessage toMfgInternalMessage(
            String system,
            String factory,
            String equipment,
            String sender,
            String hbaseTableName,
            String topic,
            String payload,
            CommonConstants.Target target,
            CommonConstants.EtlProcessType etlProcessType)
            throws IOException
    {

        return toMfgInternalMessage(
                system,
                factory,
                equipment,
                sender,
                hbaseTableName,
                topic,
                payload,
                target,
                etlProcessType,
                OBJECT_MAPPER);
    }

    public static MfgInternalMessage toMfgInternalMessage(
            String system,
            String factory,
            String equipment,
            String sender,
            String hbaseTableName,
            String topic,
            String payload,
            CommonConstants.Target target,
            CommonConstants.EtlProcessType etlProcessType,
            ObjectMapper objectMapper)
            throws IOException
    {
        Map<String, Object> entries = objectMapper.readValue(payload, Map.class);

        return toMfgInternalMessage(
                system,
                factory,
                equipment,
                sender,
                hbaseTableName,
                topic,
                entries,
                target,
                etlProcessType,
                OBJECT_MAPPER);
    }

    public static MfgInternalMessage toMfgInternalMessage(
            String system,
            String factory,
            String equipment,
            String sender,
            String hbaseTableName,
            String topic,
            Map<String, Object> entries,
            CommonConstants.Target target,
            CommonConstants.EtlProcessType etlProcessType,
            ObjectMapper objectMapper)
            throws IOException
    {
        Map<String, Object> newMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                String valueStr = (String)value;
                if (!Strings.isNullOrEmpty(valueStr)) {
                    newMap.put(field, valueStr);
                }
            } else if (value != null) {
                newMap.put(field, value.toString());
            }
        }

        MfgInternalMessage internalMessage = new MfgInternalMessage(system, factory, equipment, hbaseTableName, sender, target, newMap, etlProcessType);

        return internalMessage;
    }

}
