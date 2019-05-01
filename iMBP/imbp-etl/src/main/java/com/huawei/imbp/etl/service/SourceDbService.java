package com.huawei.imbp.etl.service;

import com.huawei.imbp.etl.common.DataType;
import com.huawei.imbp.etl.service.build.MetadataService;
import com.huawei.imbp.etl.validation.DataTypeValidation;
import com.huawei.imbp.etl.validation.FormatValidation;
import com.huawei.imbp.etl.validation.NullableValidation;
import com.huawei.imbp.etl.validation.PatternValidation;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 4/24/2019
 */

@Log4j2
public class SourceDbService implements DbService{

    public MetadataService metadataService;

    public SourceDbService(MetadataService metadataService){
        this.metadataService = metadataService;
    }

    public DataType validation(Object v, String k) throws Exception{

        DataType dataTypeEnum;

        log.debug("start "+dbDirection.SOURCE+" validation");
        try {
            Map<String, Map<String, String>> columns = metadataService.origColumns();

            Map<String, String> column = columns.get(k);
            if (column == null) {
                throw imbpEx.setMessage("not defined in source");
            }

            Object nullable = column.get("nullable");
            log.debug(k+" in "+dbDirection.SOURCE+" is nullable "+nullable);
            if(nullable != null) {
                NullableValidation.checkColumnNullable(v, nullable, dbDirection.SOURCE);
            }

            String dataType = column.get("columnDataType");

            try {
                dataTypeEnum = DataType.valueOf(dataType.toUpperCase());
                log.debug(k+" in "+dbDirection.SOURCE+" dataType is "+dataTypeEnum.name());
            }catch (Exception e){
                throw imbpEx.setMessage(dataType + "not defined in source");
            }
            DataTypeValidation.checkDataType(dataTypeEnum, v);

            String dataFormat = column.get("columnFormat");
            if (!StringUtils.isEmpty(dataFormat)) {
                log.debug(dbDirection.SOURCE+" dataFormat "+dataFormat);
                FormatValidation.checkColumnFormat(v, dataFormat, dbDirection.SOURCE, DataType.valueOf(dataType));
            }

            String dataPattern = column.get("columnPattern");
            if(!StringUtils.isEmpty(dataPattern)){
                log.debug(dbDirection.SOURCE+" columnPattern "+dataFormat);
                PatternValidation.checkColumnPattern(v, dataPattern, dbDirection.SOURCE);
            }

        }catch (Exception e){
            throw imbpEx.setMessage(e.getMessage());
        }

        return dataTypeEnum;
    }
}
