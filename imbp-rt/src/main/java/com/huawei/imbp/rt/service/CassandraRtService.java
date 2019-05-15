package com.huawei.imbp.rt.service;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.entity.AoiKey;
import com.huawei.imbp.rt.entity.DateDevice;
import com.huawei.imbp.rt.repository.AoiRepository;
import com.huawei.imbp.rt.util.StatisticManager;
import com.huawei.imbp.rt.util.WriteToFile;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Charles(Li) Cai
 * @date 5/3/2019
 */

@Component
@Log4j2
public class CassandraRtService  {

    @Autowired
    AoiRepository aoiRepository;

    public static volatile int counter = 0;

    public void getData(DateDevice dateDevice){

        String date = dateDevice.getDate();
        Set<String> deviceTypes = dateDevice.getDeviceTypes();
        int i = dateDevice.getHour();

        Iterator<String> itr = deviceTypes.iterator();

        while (itr.hasNext()) {

            String deviceType = itr.next();
            log.info(deviceType);
            for (int x = 0; x < 60; x++) {

                AoiKey aoiKey = new AoiKey();
                aoiKey.setCreatedDay(date);
                aoiKey.setDeviceType(deviceType);
                aoiKey.setHour(i);
                aoiKey.setMinute(x);
                Flux<AoiEntity> aoiEntityFlux =  aoiRepository.findByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinute(date, deviceType, i, x);
                aoiEntityFlux.collectList().map(s -> {
                    WriteToFile.writeToFile(s);
                    return s.size();
                }).subscribe(s -> {
                    String key = "created_day-" + date + ":device_type-" + deviceType + ":hour-" + i;
                    log.info(key + " size: " + s);
                    });
            }
        }

    }


}
