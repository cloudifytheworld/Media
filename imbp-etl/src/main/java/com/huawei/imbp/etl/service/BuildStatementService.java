package com.huawei.imbp.etl.service;

import com.datastax.driver.core.querybuilder.BuiltStatement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.huawei.imbp.etl.util.DataTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RefreshScope
public class BuildStatementService {

    @Value("#{${imbp.metadata.data}}")
    public Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>> metadata;

//    @Value("${request.input.table}")
//    public String iTable;
//
//    @Value("${imbp.request.input.system}")
//    public String iSystem;
//
//    @Value("${imbp.request.input.destination}")
//    public String iDestination;
//
//    @Value("${imbp.request.input.payload}")
//    public String iPayload;
//
//    @Value("${imbp.metadata.column.keyspace}")
//    public String cKeyspace;
//
//    @Value("${imbp.metadata.column.shortColumnName}")
//    public String cShortColumnName;
//
//    @Value("${imbp.metadata.column.columnDataType}")
//    public String cColumnDataType;


    public BuiltStatement buildCassandraOnInsert(Map rawData){

        String origTable = (String)rawData.get("table");
        String system = (String)rawData.get("system");
        String destination = (String)rawData.get("destination");
        Map<String, String> payload = (Map)rawData.get("payload");

        try{
            Map<String, Map<String, String>> columnMap = metadata.get(system).get(origTable).get(destination);
            Insert insert = QueryBuilder.insertInto(columnMap.values().stream().findFirst().get().get("keySpace"),
                    columnMap.values().stream().findFirst().get().get("tableName"));
            payload.forEach((k,v) ->{
                String columnName = columnMap.get(k).get("shortColumnName");
                String dataType = columnMap.get(k).get("columnDataType").toLowerCase();
                insert.value(columnName, DataTypeUtil.buildCassandraDataType(dataType, v));
            });
            return insert;
        }catch (Exception e){
            log.error("fail to build cassandra statement " +e.getMessage());
        }

        return null;
    }

}
