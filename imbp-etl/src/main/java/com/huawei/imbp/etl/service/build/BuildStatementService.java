package com.huawei.imbp.etl.service.build;

import com.datastax.driver.core.Statement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 3/7/2019
 */

@Component
@RefreshScope
@Log4j2
public class BuildStatementService {


    @Value("${image.breakImageSize}")
    public int breakImageSize;

    @Value("#{${metadata.data}}")
    public Map<String, Map<String, Map<String, Object>>> metadata;

    @Value("#{${cass.keyspaces}}")
    public Map<String, Map<String, Map<String, String>>> keySpaceMetadata;

    public Statement buildCassandraOnInsert(Map rawData) throws Exception{

        log.debug("enter buildOnInsert");
        MetadataService metadataService = new MetadataService( new InputData().checkInput(rawData));
        ManageBuildService buildService = new ProcessBuildData(keySpaceMetadata);
        return buildService.build(metadataService.buildTableMap(metadata)
                .buildTableMetadata());

    }

    public Statement buildCassandraOnInsertLarge(Map rawData) throws Exception{

        log.debug("enter buildOnInsertLarge");
        MetadataService metadataService = new MetadataService( new InputData().checkInput(rawData));
        ManageBuildService buildService = new ProcessBuildLargeData(breakImageSize, keySpaceMetadata);
        return buildService.build(metadataService.buildTableMap(metadata)
                .buildTableMetadata());

    }
}
