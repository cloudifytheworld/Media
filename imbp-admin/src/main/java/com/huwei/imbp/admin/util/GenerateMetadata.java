package com.huwei.imbp.admin.util;

import com.huawei.mfg.bean.BaseMappingBean;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class GenerateMetadata {

    public static <T extends BaseMappingBean> void handleDB(String dbName, String systemName, Map<String, List<T>> dbBaseMapping,
                                Map<String, Map<String, Map<String, Map<String, BaseMappingBean>>>> metadata){

        try {
            if (dbBaseMapping == null || dbBaseMapping.size() == 0) return;

            Map<String, Map<String, Map<String, BaseMappingBean>>> origTable = metadata.get(systemName);
            if (origTable == null) {
                origTable = new HashMap<>();
            }
            metadata.put(systemName, origTable);

            for (String table : dbBaseMapping.keySet()) {

                List<BaseMappingBean> beans = (List<BaseMappingBean>)dbBaseMapping.get(table);
                Map<String, BaseMappingBean> columns = beans.stream().collect(
                        Collectors.toMap(BaseMappingBean::getColumnName, col -> col)
                );
                String origTableName = beans.get(0).getOrigTableName();

                Map<String, Map<String, BaseMappingBean>> db = origTable.get(origTableName);
                if (db == null) {
                    db = new HashMap<>();
                }
                db.put(dbName, columns);
                origTable.put(origTableName, db);
            }
        }catch (Exception e){
            log.error("Generate metadata "+e.getMessage());
        }
    }

}
