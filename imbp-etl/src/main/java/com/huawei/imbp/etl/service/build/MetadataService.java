package com.huawei.imbp.etl.service.build;

import com.huawei.imbp.etl.util.DataUtil;
import com.huawei.imbp.etl.common.ImbpException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author Charles(Li) Cai
 * @date 3/22/2019
 */
@Log4j2
public class MetadataService {

    private Map<String, Object> origTableMap;
    private Map<String, Map<String, String>> columns;
    private Map<String, String> tableMetadata;
    private Set<String> primaryKeys;
    private InputData inputData;
    public ImbpException e = new ImbpException();

    public MetadataService(InputData inputData){
        this.inputData = inputData;
    }

    public String getKeyspace() throws Exception{

        String keyspace = tableMetadata.get("keyspace");
        if(StringUtils.isEmpty(keyspace)) throw e.setEmpty("keyspace");
        log.debug("mapped keyspace "+keyspace);
        return keyspace;
    }

    public String getTableName() throws Exception{

        String tableName = tableMetadata.get("tableName");
        if(StringUtils.isEmpty(tableName)) throw e.setEmpty("tableName");
        log.debug("mapped table "+tableName);
        return tableName;
    }

    public MetadataService buildTableMap(final Map<String, Map<String, Map<String, Object>>> metadata) throws Exception{

        String system = inputData.getSystem();
        Map<String, Map<String, Object>> origSystem = metadata.get(system);
        if(origSystem == null || origSystem.size() == 0) throw e.setNotMapping("system "+system);
        log.debug(system+" contained mapped systems "+origSystem.keySet());
        String table = inputData.getTable();
        this.origTableMap = origSystem.get(table);
        if(origTableMap == null || origTableMap.size() == 0) throw e.setNotMapping("table "+table);
        return this;
    }

    private Map<String, Map<String, String>> origColumns() throws Exception{

        Map<String, Map<String, String>> origColumns = Map.class.cast(origTableMap.get("columns"));
        if(origColumns == null || origColumns.size() == 0) throw e.setEmpty("originalColumns");
        log.debug("original column keys"+origColumns.keySet());
        return origColumns;
    }

    private Map<String, Map<String, String>> mappedColumns() throws Exception{

        String destination = inputData.getDestination().toLowerCase();
        Map<String, Map<String, String>> mappedColumns = Map.class.cast(origTableMap.get(destination));
        if(mappedColumns == null || mappedColumns.size() == 0) throw e.setMissing("mappedColumns");
        log.debug(destination+" contains column keys "+mappedColumns.keySet());
        return mappedColumns;
    }

    public MetadataService buildTableMetadata() throws Exception{

        this.columns = mappedColumns();
        this.tableMetadata = columns.get("tableMetadata");
        if(tableMetadata == null || tableMetadata.size() == 0)
            throw e.setEmpty("tableMetadata");
        log.debug("mapped tableMetadata keys "+tableMetadata.keySet());
        log.debug("mapped tableMetadata values "+tableMetadata.values());
        if(columns.size() == 1){
            this.columns = origColumns();
        }
        return primaryKeys();
    }

    private MetadataService primaryKeys() throws Exception{

        String keyStr = tableMetadata.get("primaryKeys");
        if(StringUtils.isEmpty(keyStr)) throw e.setEmpty("primaryKeys");
        this.primaryKeys = DataUtil.getPrimaryKey(keyStr);
        log.debug("primary keys mapped "+primaryKeys.toString());
        return this;
    }

    public MetadataService addMustFieldCheck(final String field) throws Exception{

        if(primaryKeys.size() == 0){
            buildTableMetadata();
        }
        primaryKeys.add(field);
        return this;
    }

    public Map<String, Map<String, String>> getColumns() {
        return columns;
    }


    public Set<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public InputData getInputData() {
        return inputData;
    }
}
