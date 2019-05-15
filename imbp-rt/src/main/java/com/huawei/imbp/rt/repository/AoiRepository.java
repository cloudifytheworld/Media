package com.huawei.imbp.rt.repository;

import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.entity.AoiKey;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * @author Charles(Li) Cai
 * @date 5/14/2019
 */


public interface AoiRepository extends ReactiveCassandraRepository<AoiEntity, AoiKey> {

    Flux<Slice<AoiEntity>> findByKey(AoiKey aoiKey, Pageable pageable);

    Flux<AoiEntity> findByKeyCreatedDayAndKeyDeviceTypeAndKeyHourAndKeyMinute(String createdDay, String deviceType, int hour, int minute);
}
