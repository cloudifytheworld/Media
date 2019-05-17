package com.huawei.imbp.rt.repository;

import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.entity.AoiKey;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;


/**
 * @author Charles(Li) Cai
 * @date 5/14/2019
 */


public interface AoiRepository extends ReactiveCassandraRepository<AoiEntity, AoiKey> {

    @AllowFiltering
    Flux<AoiEntity> findTop2ByKeyCreatedDayAndKeyDeviceType(String createdDay, String deviceType);


    @AllowFiltering
    Flux<AoiEntity> findAllByKeyCreatedDayAndKeyDeviceType(String createdDay, String deviceType);

    @AllowFiltering
    Flux<AoiEntity> findTop2ByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinuteAndKeyLabelAndKeyCreatedTimeLessThan(
            String createdDay, String deviceType, Integer hour, Integer minute, String label, Date createdTime);

    @AllowFiltering
    Flux<AoiEntity> findByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinute(String createdDay, String deviceType, int hour, int minute);

    @AllowFiltering
    Mono<AoiEntity> findByKeyCreatedDayAndKeyDeviceType(String createdDay, String deviceType);
}
