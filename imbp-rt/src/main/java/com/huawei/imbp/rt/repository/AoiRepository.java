package com.huawei.imbp.etl.repository;

import com.huawei.imbp.etl.entity.AoiEntity;
import com.huawei.imbp.etl.entity.AoiKey;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;


/**
 * @author Charles(Li) Cai
 * @date 5/13/2019
 */


public interface AoiRepository extends ReactiveCassandraRepository<AoiEntity, AoiKey> {

}
