package com.huawei.mfg.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.huawei.mfg.pojo.MfgInternalMessage;
import com.huawei.mfg.util.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasePerformanceTest {
    private static Logger logger = LoggerFactory.getLogger(BasePerformanceTest.class);
    protected static final int CONNECT_TIMEOUT = 30000;
    protected static final int READ_TIMEOUT    = 30000;
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected static final CommonConstants.Target[] targets = {
            CommonConstants.Target.HBASE,
            CommonConstants.Target.HDFS,
            CommonConstants.Target.KAFKA,
            CommonConstants.Target.SQL
    };

    protected static final String[] targetNames = {
            CommonConstants.Target.HBASE.name(),
            CommonConstants.Target.HDFS.name(),
            CommonConstants.Target.KAFKA.name(),
            CommonConstants.Target.SQL.name()
    };


    public BasePerformanceTest() {
    }

    protected MfgInternalMessage toMfgInternalMessage(String system,
                                                      String factory,
                                                      String equipment,
                                                      String hbaseTableName,
                                                      String sender,
                                                      String topic,
                                                      String payload,
                                                      CommonConstants.Target target,
                                                      Map<String, String> dateFields,
                                                      CommonConstants.EtlProcessType etlProcessType)
            throws IOException
    {
        // from string toMapObject list of objects
        Map<String, Object> entries = objectMapper.readValue(payload, Map.class);

        // convert all fields' values toMapObject String, for example, double or integer toMapObject string
        Map<String, Object> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            //for date field
            if (value instanceof String) {
                String valueStr = (String)value;
                if (!Strings.isNullOrEmpty(valueStr)) {
                    newMap.put(field, valueStr);
                }
            } else if (value != null) {
                newMap.put(field, value.toString());
            }
        }

        MfgInternalMessage msg = new MfgInternalMessage(system, factory, equipment, hbaseTableName, sender, target, newMap, etlProcessType);

        return msg;
    }

    protected List<Map<String, Object>> loadDataAsMap(String sampleDataDir, String dataFile) {
        File file = new File(sampleDataDir);
        if (!file.exists()) {
            logger.warn("Input directory({}) doesn't exist", sampleDataDir);
            return null;
        }

        String fullFilename = sampleDataDir + File.separator + dataFile;
        logger.debug("Loading {} ...", fullFilename);

        List<Map<String, Object>> mapList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fullFilename))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                Map<String, Object> map = objectMapper.readValue(line, Map.class);
                mapList.add(map);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapList;
    }

    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) { }
    }

}
