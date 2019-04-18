package com.huawei.imbp.etl.service.build;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Statement;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 3/26/2019
 */

@Log4j2
public abstract class ParseBuildData implements ManageBuildService {

    public Statement parseData(MetadataService metadataService) throws Exception{


        InputData inputData = metadataService.getInputData();
        Object payload = inputData.getPayload();
        if(List.class.isInstance(payload)) {
            List<Map<String, Object>> payloads = List.class.cast(payload);
            if (payloads == null || payloads.size() == 0) throw e.setEmpty("payloads");
            BatchStatement batchStatement = new BatchStatement();

            payloads.stream().forEach( load ->{
                try {
                    log.debug("enter batch build");
                    batchStatement.add(buildInsertQuery(load, metadataService));
                }catch (Exception ex){
                    log.error(ex.getMessage());
                }
            });
            if(batchStatement.size() == 0){
                throw e.setMessage("no data insert on batch, check log");
            }
            return batchStatement;
        }if(Map.class.isInstance(payload)) {
            log.debug("enter single build");
            return buildInsertQuery((Map)payload, metadataService);
        }else{
            throw e.setMessage("format not supported");
        }
    }

    public abstract Statement buildInsertQuery(final Map<String, Object> payload, MetadataService metadataService)
            throws Exception;
}
