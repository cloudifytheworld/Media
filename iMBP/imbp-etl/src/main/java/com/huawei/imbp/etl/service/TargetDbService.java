package com.huawei.imbp.etl.service;

import com.huawei.imbp.etl.common.DataType;
import com.huawei.imbp.etl.service.build.MetadataService;
import com.huawei.imbp.etl.transform.Conversion;
import com.huawei.imbp.etl.transform.ManageConversion;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */

@Log4j2
public class TargetDbService implements DbService{

    MetadataService metadataService;
    Map<String, Map<String, Map<String, String>>> keySpaceMetadata;
    ManageConversion mgCon = new ManageConversion();

    public TargetDbService(MetadataService metadataService, Map<String, Map<String, Map<String, String>>> keySpaceMetadata){
        this.metadataService = metadataService;
        this.keySpaceMetadata = keySpaceMetadata;
    }

    public Object transformation(DataType sourceDataType, Object v, Map<String, String> targetColumn, Map<String, String> sourceColumn) throws Exception{

        log.debug("start "+dbDirection.TARGET + " transformation and conversion process");

        String keySpace = metadataService.getKeyspace();
        String tableName = metadataService.getTableName();
        String columnName = targetColumn.get("columnName");
        String targetDataType;

        try{
            targetDataType = keySpaceMetadata.get(keySpace).get(tableName).get(columnName);
        }catch (Exception ex){
            throw imbpEx.setMessage("missing in CASS table");
        }

        if(targetDataType == null) throw imbpEx.setMessage(columnName+" is not in CASS table");

        String targetFormat = targetColumn.get("columnFormat");
        String sourceFormat = sourceColumn.get("columnFormat");
        Conversion conversion = mgCon.getConversion(DataType.valueOf(targetDataType));
        Object data = conversion.convert(sourceDataType, v, targetFormat, sourceFormat);


        return data;
    }
}
