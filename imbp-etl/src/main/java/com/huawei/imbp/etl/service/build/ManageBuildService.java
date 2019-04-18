package com.huawei.imbp.etl.service.build;

import com.datastax.driver.core.Statement;
import com.huawei.imbp.etl.common.ImbpException;

/**
 * @author Charles(Li) Cai
 * @date 3/22/2019
 */
public interface ManageBuildService {

    ImbpException e = new ImbpException();
    Statement build(MetadataService metadataService) throws Exception;

}
