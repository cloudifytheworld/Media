package com.huawei.imbp.etl.service.build;

import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.common.base.Throwables;
import com.huawei.imbp.etl.util.DataType;
import com.huawei.imbp.etl.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.sql.Timestamp;
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
        if(keySpaceMetadata == null || keySpaceMetadata.size() == 0){
            log.error("keySpaceMetadata is empty or not there");
        }
    }

    public Statement build(MetadataService metadataService) throws Exception{
        return parseData(metadataService);
    }


    public Statement buildInsertQuery(final Map<String, Object> payload, MetadataService metadataService) throws Exception{

        Insert insert = QueryBuilder.insertInto(metadataService.getKeyspace(), metadataService.getTableName());
        Set<String> primaryKeys = metadataService.getPrimaryKeys();
        Map<String, Map<String, String>> columns = metadataService.getColumns();
        String keySpace = metadataService.getKeyspace();
        String tableName = metadataService.getTableName();
        String dataType = null;

        for(String k : columns.keySet()){
            try {
                if (k.equals("tableMetadata")) continue;
                Map<String, String> column = columns.get(k);

                Object v = payload.get(k);
                if (v == null) continue;
                log.debug("Mapped key " + k + "--" + "Payload value " + v);
                DataUtil.checkInputData(v, column);

                String columnName = column.get("columnName");
                try{
                    dataType = keySpaceMetadata.get(keySpace).get(tableName).get(columnName);
                }catch (Exception ex){
                    log.error(columnName+ " can't find dataType from CASS metadata table");
                }
                if(StringUtils.isEmpty(dataType)) {
                    dataType = column.get("columnDataType");
                }
                log.debug("Mapped ColumnName " + columnName + "--" + "Mapped DataType " + dataType);
                if (primaryKeys.remove(columnName)) {
                    if (StringUtils.isEmpty(v.toString().trim())) {
                        primaryKeys.add(columnName);
                        continue;
                    }
                }


                //insert.value(columnName, DataType.buildDataType(column, dataType, v));

                Object obj = DataType.buildDataType(column, dataType, v);
                insert.value(columnName, obj);
                if(Timestamp.class.isInstance(obj)){
                    Timestamp timestamp = (Timestamp)obj;
                    DateTime dateTime = new DateTime(timestamp.getTime());
                    log.debug("hour: "+dateTime.getHourOfDay());
                    int minutes = dateTime.getMinuteOfHour();
                    int mins = minutes/10;
                    insert.value("hour", dateTime.getHourOfDay());
                    insert.value("mins", mins);
                }

            }catch (Exception ex){
                throw e.setMessage(k+" "+ex.getMessage());
            }
        }

        if(primaryKeys.size() > 0) throw e.setEmptyOrMissingOrMapping(primaryKeys.toString());

        return insert;
    }
}
