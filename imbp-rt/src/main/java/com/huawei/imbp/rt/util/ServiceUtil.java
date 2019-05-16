package com.huawei.imbp.rt.util;

import com.huawei.imbp.rt.common.ImbpException;
import com.huawei.imbp.rt.common.InputParameter;
import com.huawei.imbp.rt.entity.AoiKey;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.Optional;

import static com.huawei.imbp.rt.common.Constant.IMAGE_PAGE_SIZE;

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

        Optional<String> page = request.queryParam("page");
        Optional<String> size = request.queryParam("size");

        if(page.isPresent()){
            Integer pageN = DataUtil.checkValidInteger(page.get());
            input.setPage(pageN == null?0:pageN);
            input.setSize(size.isPresent()?DataUtil.checkValidInteger(size.get()): IMAGE_PAGE_SIZE);
        }

        return input;
    }
}
