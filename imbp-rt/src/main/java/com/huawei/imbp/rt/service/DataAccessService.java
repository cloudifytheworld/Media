package com.huawei.imbp.rt.service;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSetFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Charles(Li) Cai
 * @date 6/12/2019
 */
@Component
public abstract class DataAccessService {


    public abstract void execute(AtomicInteger count, BoundStatement boundStatement, int indexSize,
    List<ResultSetFuture> futuresData, QueueService<String> queue);
}
