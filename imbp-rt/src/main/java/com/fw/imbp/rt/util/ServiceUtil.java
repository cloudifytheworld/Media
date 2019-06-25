package com.fw.imbp.rt.util;

import com.fw.imbp.rt.common.ImbpException;
import com.fw.imbp.rt.common.InputParameter;
import com.fw.imbp.rt.entity.AoiKey;

import org.springframework.web.reactive.function.server.ServerRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @author Charles(Li) Cai
 * @date 5/14/2019
 */
public class ServiceUtil {

    static final ImbpException imbp = new ImbpException();

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

    public static InputParameter getInputParam(ServerRequest request) throws Exception{

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        InputParameter input = new InputParameter();

        Optional<String> system = request.queryParam("system");
        if(!system.isPresent()){
            throw imbp.setMessage("must specify which system to retrieve");
        }
        input.setSystem(system.get());

        Optional<String> from = request.queryParam("from");
        if(!from.isPresent()){
            throw imbp.setMessage("must specify which date to start");
        }
        String[] date = DataUtil.convertStringToArray(from.get());
        input.setFrom(date);

        Optional<String> device_type = request.queryParam("deviceType");
        if(device_type.isPresent()){
            input.setDeviceType(device_type.get());
        }

        Optional<String> to = request.queryParam("to");
        if(to.isPresent()){
            input.setTo(to.get());
        }

        Optional<String> hour = request.queryParam("hour");
        if(hour.isPresent()){
            input.setHour(DataUtil.checkValidInteger(hour.get()));
        }

        Optional<String>  minute = request.queryParam("minute");
        if(minute.isPresent()){
            input.setMinute(DataUtil.checkValidInteger(minute.get()));
        }

        Optional<String> label = request.queryParam("label");
        if(label.isPresent()){
            input.setLabel(label.get());
        }

        Optional<String> createdTime = request.queryParam("createdTime");
        if(createdTime.isPresent()){
            Date dateTime = dateFormat.parse(createdTime.get());
            input.setCreatedTime(dateTime);
        }

        return input;
    }
}
