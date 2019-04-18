package com.huawei.imbp.etl.service.build;

import com.huawei.imbp.etl.common.ImbpException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 3/12/2019
 */

public class InputData {

    public String table;
    public String system;
    public String destination;
    public Object payload;
    public Map<String, Object> rawData;
    public ImbpException e = new ImbpException();


    public InputData checkTable() throws Exception{

        table = (String)rawData.get("table");
        if(StringUtils.isEmpty(table)) throw e.setEmpty("table");
        return this;
    }

    public InputData checkSystem() throws Exception{

        this.system = (String)rawData.get("system");
        if(StringUtils.isEmpty(system)) throw e.setEmpty("system");
        return this;
    }

    public InputData checkDestination() throws Exception{

        this.destination = (String)rawData.get("destination");
        if(StringUtils.isEmpty(destination)) throw e.setEmpty("destination");
        return this;
    }

    public InputData checkPayload() throws Exception{

        this.payload = rawData.get("payload");
        if(payload == null) throw e.setEmpty("payload");
        return this;
    }

    public InputData checkInput(final Map rawData) throws Exception{

        this.rawData = rawData;
        if(rawData == null || rawData.size() == 0) throw e.setEmpty("body");
        checkTable().checkSystem().checkDestination().checkPayload();
        return this;
    }

    public String getTable() {
        return table;
    }

    public String getSystem() {
        return system;
    }

    public String getDestination() {
        return destination;
    }

    public Object getPayload() {
        return payload;
    }

    public Map<String, Object> getRawData() {
        return rawData;
    }
}
