package com.huawei.mfg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@SuppressWarnings("Duplicates")
final public class GenerateMetadata {
    private static Logger LOG = LoggerFactory.getLogger(GenerateMetadata.class);
    public static final String COLUMNS = "columns";
    public static final String CASSANDRA = "cassandra";
    public static final String HBASE = "hbase";
    public static final String TOPIC = "topic";
    public static final String HIVE = "hive";

    public static <T> void handleDB(String dbName, String systemName, Map<String, Map<String, T>> cassandraMapping,
                                    Map<String, Map<String, Map<String, Map<String, Object>>>> mfgMetadataMappings,
                                    Map<String, Map<String, Object>> tableColumnMap)
    {
        try {
            if (cassandraMapping == null || cassandraMapping.size() == 0) return;

            Map<String, Map<String, Map<String, Object>>> productTableColMap = mfgMetadataMappings.get(systemName);
            if (productTableColMap == null) {
                productTableColMap = new HashMap<>();
                mfgMetadataMappings.put(systemName, productTableColMap);
            }

            for (String srcTableName : cassandraMapping.keySet()) {
                Map<String, Object> cols = (Map<String, Object>)cassandraMapping.get(srcTableName);
                Map<String, Map<String, Object>> db = productTableColMap.get(srcTableName);
                if (db == null) {
                    db = new HashMap<>();
                }

                if (tableColumnMap != null) {
                    db.put(COLUMNS, tableColumnMap.get(srcTableName));
                }
                db.put(dbName, cols);
                productTableColMap.put(srcTableName, db);
            }
        }
        catch (Exception e){
            LOG.error("Generate metadata in {} for {} \n {}", dbName, systemName, e.getMessage());
        }
    }

}
