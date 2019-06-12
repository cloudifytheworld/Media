package com.huawei.imbp.etl.build;

import com.huawei.imbp.etl.common.ImbpException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Charles(Li) Cai
 * @date 6/11/2019
 */

@Component
@Log4j2
public class StatementBuildFactory {


    @Autowired
    AoiBuildStatement aoiBuild;

    ImbpException imbp = new ImbpException();

    public BuildStatement get(String system) throws Exception{

        switch (OnSystem.valueOf(system)){
            case aoi:
                return aoiBuild;
            default:
                throw imbp.setMessage("system is not supported yet");
        }
    }
}
