package com.huawei.imbp.rt.util;

import com.huawei.imbp.rt.entity.AoiKey;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Optional;

/**
 * @author Charles(Li) Cai
 * @date 5/14/2019
 */
public class ServiceUtil {
    
    
    public static AoiKey getAoiKey(ServerRequest request){
        
        AoiKey aoiKey = new AoiKey();

        Optional<String> created_day = request.queryParam("created_day");
        if(created_day.isPresent()){
            aoiKey.setCreatedDay(created_day.get());
        }

        Optional<String> device_type = request.queryParam("device_type");
        if(device_type.isPresent()){
            aoiKey.setDeviceType(device_type.get());
        }

        return aoiKey;
        
    }
}
