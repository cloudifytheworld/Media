package com.huawei.imbp.etl.service;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 3/25/2019
 */

@Component
@Log4j2
@RefreshScope
public class DataRepository {

    @Autowired
    public ListenableFuture<Session> session;

    @Autowired
    public LoggingService loggingService;

    public void execute(Statement statement, Map rawData){

        log.debug(statement.toString());

        Futures.addCallback(Futures.transformAsync(session,
                (Session session) -> session.executeAsync(statement)
        ), new FutureCallback<ResultSet>() {

            @Override
            public void onSuccess(ResultSet result) {
                log.debug("Insert SUCCESS");
            }

            @Override
            public void onFailure(Throwable t) {
                loggingService.onFailure("Error from cassandra database "+t.getMessage(), rawData);
            }
        });
    }
}
