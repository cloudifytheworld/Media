package com.huawei.imbp.rt.build;

import com.huawei.imbp.rt.common.ImbpException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Charles(Li) Cai
 * @date 6/12/2019
 */

@Component
@Log4j2
public class StatementBuildFactory {


    @Autowired
    AoiDateStatement aoiDateStatement;

    @Autowired
    AoiDateTimeStatement aoiDateTimeStatement;


    ImbpException imbp = new ImbpException();

    public BuildStatement get(String system, boolean range) throws Exception{

        log.debug(String.format("build system %s the range set is %s ", system, range+""));

        switch (OnSystem.valueOf(system)){
            case aoi:
                return range? aoiDateTimeStatement :aoiDateStatement;
            default:
                throw imbp.setMessage("system is not supported yet");
        }
    }
}
