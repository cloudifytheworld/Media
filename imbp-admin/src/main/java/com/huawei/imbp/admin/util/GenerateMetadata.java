package com.huawei.imbp.admin.util;

import com.huawei.mfg.bean.*;
import com.huawei.mfg.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.huawei.imbp.admin.util.Constant.COLUMNS;

/**
 * @author Charles(Li) Cai
 * @date 2/28/2019
 */

@Slf4j
public class GenerateMetadata {

    public static <T> void handleDB(String dbName, MfgSystem mfgSystem, Map<String, Map<String, T>> dbBaseMapping,
                                Map<String, Map<String, Map<String, Map<String, Object>>>> metadata,
                                Map<String, Map<String, Object>> tableColumns){

        try {
            if (dbBaseMapping == null || dbBaseMapping.size() == 0) return;

            Map<String, Map<String, Map<String, Object>>> origTable = metadata.get(mfgSystem.getName());
            if (origTable == null) {
                origTable = new HashMap<>();
                metadata.put(mfgSystem.getName(), origTable);
            }

            for (String origTableName : dbBaseMapping.keySet()) {
                Map<String, Object> cols = (Map<String, Object>)dbBaseMapping.get(origTableName);
                Map<String, Map<String, Object>> db = origTable.get(origTableName);
                if (db == null) {
                    db = new HashMap<>();
                }
                if(tableColumns != null){
                    db.put(COLUMNS, tableColumns.get(origTableName));
                }
                db.put(dbName, cols);
                origTable.put(origTableName, db);
            }
        }catch (Exception e){
            log.error("Generate metadata in {} for {} \n {}", dbName, mfgSystem.getName(), e.getMessage());
        }
    }

    public static void handleColumns(String columnsName,  MfgSystem mfgSystem, Map<String, Map<String, Object>> tableColumns,
                                   Map<String, Map<String, Map<String, Map<String, Object>>>> metadata) {
        try{
            if (tableColumns == null || tableColumns.size() == 0) return;

            Map<String, Map<String, Map<String, Object>>> origTable = metadata.get(mfgSystem.getName());
            if (origTable == null) {
                origTable = new HashMap<>();
            }
            metadata.put(mfgSystem.getName(), origTable);

            for(String origTableName : tableColumns.keySet()){
                Map<String, Object> cols = tableColumns.get(origTableName);
                Map<String, Map<String, Object>> metaTable = origTable.get(origTableName);
                if(metaTable == null) {
                    metaTable = new HashMap<>();
                }
                metaTable.put(columnsName, cols);
                origTable.put(origTableName, metaTable);
            }

        }catch (Exception e){
            log.error("Generate metadata in columns for {} \n {}", mfgSystem.getName(), e.getMessage());
        }
    }

    public static void handleTopic(String topicName,  String systemName, List<Pair<MfgSystemTable, MfgSystemTableTopic>> topicMapping,
                                   Map<String, Map<String, Map<String, String>>> metadata){


        try{
            if (topicMapping == null || topicMapping.size() == 0) return;

            Map<String, Map<String, String>> origTable = metadata.get(systemName);
            if (origTable == null) {
                origTable = new HashMap<>();
            }
            metadata.put(systemName, origTable);
            for(int i=0; i<topicMapping.size(); i++){
                String origTableName = topicMapping.get(i).getFirst().getTableName();
                Map<String, String> topicMap = origTable.get(origTableName);
                if(topicMap == null){
                    topicMap = new HashMap<>();
                }
                topicMap.put(topicName, topicMapping.get(i).getSecond().getTopic());
                origTable.put(origTableName, topicMap);
            };

        }catch (Exception e){
            log.error("Generate metadata in topic for {} \n {}", systemName, e.getMessage());
        }
    }
}
