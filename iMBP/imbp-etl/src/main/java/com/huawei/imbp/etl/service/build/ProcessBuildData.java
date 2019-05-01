package com.huawei.imbp.etl.service.build;

import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.common.base.Throwables;
import com.huawei.imbp.etl.service.SourceDbService;
import com.huawei.imbp.etl.service.TargetDbService;
import com.huawei.imbp.etl.util.DataType;
import com.huawei.imbp.etl.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author Charles(Li) Cai
 * @date 3/7/2019
 */

@Log4j2
public class ProcessBuildData extends ParseBuildData {


    Map<String, Map<String, Map<String, String>>> keySpaceMetadata;

    public ProcessBuildData(Map<String, Map<String, Map<String, String>>> keySpaceMetadata){
        this.keySpaceMetadata = keySpaceMetadata;
    }

    public Statement build(MetadataService metadataService) throws Exception{
        return parseData(metadataService);
    }


    public Statement buildInsertQuery(final Map<String, Object> payload, MetadataService metadataService) throws Exception{

        SourceDbService sourceDbService = new SourceDbService(metadataService);
        TargetDbService targetDbService = new TargetDbService(metadataService, keySpaceMetadata);

        Insert insert = QueryBuilder.insertInto(metadataService.getKeyspace(), metadataService.getTableName());
        Set<String> primaryKeys = metadataService.getPrimaryKeys();
        Map<String, Map<String, String>> targetColumns = metadataService.getColumns();
        Map<String, Map<String, String>> sourceColumns = metadataService.getColumns();
//        String keySpace = metadataService.getKeyspace();
//        String tableName = metadataService.getTableName();
//        String dataType;

        for(String k : targetColumns.keySet()){
            try {
                if (k.equals("tableMetadata")) continue;
                Map<String, String> targetColumn = targetColumns.get(k);
                Map<String, String> sourceColumn = sourceColumns.get(k);

                if(targetColumn == null || targetColumn.size() == 0){
                    throw e.setMessage("empty or not mapped in metadata");
                }
                String columnName = targetColumn.get("columnName");

                Object v = payload.get(k);
                if (v == null) continue;
                log.debug("Mapped key " + k + "--" + "Payload value " + v);
                //DataUtil.checkInputData(v, column);
                Object data = targetDbService.transformation(sourceDbService.validation(v, k), v, targetColumn, sourceColumn);
//
//                try{
//                    dataType = keySpaceMetadata.get(keySpace).get(tableName).get(columnName);
//                }catch (Exception ex){
//                    throw e.setMessage("missing in CASS table");
//                }

//                log.debug("Mapped ColumnName " + columnName + "--" + "Mapped DataType " + dataType);
                if (primaryKeys.remove(columnName)) {
                    if (StringUtils.isEmpty(v.toString().trim())) {
                        primaryKeys.add(columnName);
                        continue;
                    }
                }

//                insert.value(columnName, DataType.buildDataType(column, dataType, v));
                insert.value(columnName, data);

            }catch (Exception ex){
                throw e.setMessage(k+" "+ex.getMessage());
            }
        }

        if(primaryKeys.size() > 0) throw e.setEmptyOrMissingOrMapping(primaryKeys.toString());

        return insert;
    }
}
