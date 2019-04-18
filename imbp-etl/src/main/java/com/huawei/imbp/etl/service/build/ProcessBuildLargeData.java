package com.huawei.imbp.etl.service.build;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.huawei.imbp.etl.util.DataType;
import com.huawei.imbp.etl.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author Charles(Li) Cai
 * @date 3/22/2019
 */

@Log4j2
public class ProcessBuildLargeData extends ParseBuildData {


    int breakImageSize;
    Map<String, Map<String, Map<String, String>>> keySpaceMetadata;

    public ProcessBuildLargeData(int breakImageSize, Map<String, Map<String, Map<String, String>>> keySpaceMetadata){

        this.breakImageSize = breakImageSize;
        if(breakImageSize == 0){
            log.error("breakImageSize should bigger than 0");
        }
        this.keySpaceMetadata = keySpaceMetadata;
        if(keySpaceMetadata == null || keySpaceMetadata.size() == 0){
            log.error("keySpaceMetadata is empty or not there");
        }
    }

    @Override
    public Statement build(MetadataService metadataService) throws Exception {
        return parseData(metadataService);
    }

    public BatchStatement buildInsertQuery(final Map<String, Object> payload, MetadataService metadataService) throws Exception{

        String keySpace = metadataService.getKeyspace();
        String tableName = metadataService.getTableName();
        Set<String> primaryKeys = metadataService.getPrimaryKeys();
        primaryKeys.remove("part");
        Map<String, Map<String, String>> columns = metadataService.getColumns();
        List<String> names = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        int imageIndex = 0;

        for(String k : columns.keySet()){
            try {
                if (k.equals("tableMetadata") || k.equals("part")) continue;
                Map<String, String> column = columns.get(k);

                Object v = payload.get(k);
                if (v == null) continue;
                log.debug("Mapped key " + k + "--" + "Payload value " + v);
                DataUtil.checkInputData(v, column);

                String dataType = null;
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
                    if (StringUtils.isEmpty(v.toString())) {
                        primaryKeys.add(columnName);
                        continue;
                    }
                }

                names.add(columnName);
                Object value = DataType.buildDataType(column, dataType, v);

                if (ByteBuffer.class.isInstance(value)) {
                    imageIndex = names.size() - 1;
                }
                values.add(value);
            }catch (Exception ex){
                throw e.setMessage(k+" "+ex.getMessage());
            }
        }

        if(primaryKeys.size() > 0) throw e.setEmptyOrMissingOrMapping(primaryKeys.toString());
        BatchStatement btm = buildInsertData(imageIndex, names, values, metadataService);
        return btm;
    }

    private BatchStatement buildInsertData(int imageIndex, List<String> names, List<Object> values,
                                            MetadataService metadataService) throws Exception{

        String keySpace = metadataService.getKeyspace();
        String tableName = metadataService.getTableName();
        ByteBuffer buffer = (ByteBuffer)values.get(imageIndex);
        byte[] image = buffer.array();
        int size = buffer.capacity();
        int parts = DataUtil.roundupToNextInt((float)size/breakImageSize);
        names.add("part");

        BatchStatement btm = new BatchStatement();

            for (int i = 0; i < parts; i++) {
                try {
                    Insert insert = QueryBuilder.insertInto(keySpace, tableName);
                    List<Object> breakList = new ArrayList<>();
                    int nextSize = breakImageSize * (i + 1);
                    int len = (size / nextSize) >= 1 ? nextSize : size;
                    byte[] bytes = Arrays.copyOfRange(image, breakImageSize * i, len);
                    for (int j = 0; j < values.size(); j++) {
                        if (j == imageIndex) {
                            breakList.add(ByteBuffer.wrap(bytes));
                            continue;
                        }
                        breakList.add(values.get(j));
                    }
                    breakList.add(i);

                    insert.values(names, breakList);
                    btm.add(insert);
                }catch (Exception ex){
                    String data = keySpace+" "+tableName+" "+"part "+i;
                    throw e.setMessage(data+" "+ex.getMessage());
                }
            }

        return btm;
    }
}
