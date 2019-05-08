package com.huawei.imbp.imbprt.service;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.imbp.imbprt.entity.DateDevice;
import com.huawei.imbp.imbprt.entity.RowsKey;
import com.huawei.imbp.imbprt.util.StatisticManager;
import com.huawei.imbp.imbprt.util.WriteToFile;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * @author Charles(Li) Cai
 * @date 5/3/2019
 */

@Component
@Log4j2
public class CassandraRtService extends DataRepository{




    public static volatile int counter = 0;

    public void getData(DateDevice dateDevice){
        PreparedStatement statement = session.prepare("SELECT * FROM images.aoi_single_component_image where created_day = ? and device_type = ? and hour = ? and mins = ? ALLOW FILTERING");

        List<ResultSetFuture> futuresData = new ArrayList<>();
        String date = dateDevice.getDate();
        Set<String> deviceTypes = dateDevice.getDeviceTypes();
        int i = dateDevice.getHour();

        Iterator<String> itr = deviceTypes.iterator();
        //final Semaphore semaphore = new Semaphore(6);

        while (itr.hasNext()) {
            try {
                String deviceType = itr.next();
//                for (int i = 0; i < 13; i++) {
                    for (int x = 0; x < 60; x++) {
                        //semaphore.acquire();
                        ResultSetFuture resultSetFuture = session.executeAsync(statement.bind(date, deviceType, i, x));
                        futuresData.add(resultSetFuture);
                    }

                    List<ListenableFuture<ResultSet>> futureLists = Futures.inCompletionOrder(futuresData);

                    for (ListenableFuture<ResultSet> future : futureLists) {
                        try {
                            ResultSet rs = future.get();
                            List<Row> rows = rs.all();
                            int size = rows.size();
                            if (size > 0) {
                                String key = "created_day-" + date + ":device_type-" + deviceType + ":hour-" + i;
                                log.info(key + " size: " + size);
                                StatisticManager.put(key, size);
                                //WriteToFile.writeToFile(rows, "created_day-" + date + ":device_type-" + deviceType, ":hour", i);
                                //WriteToFile.writeToFile(rows, key);
//                                    RowsKey rowsKey = new RowsKey();
//                                    rowsKey.setRows(rows);
//                                    rowsKey.setKey("created_day-"+date+":device_type-"+deviceType);
//                                    rowsKey.setHour(":hour");
//                                    rowsKey.setWhich(i);
//
//                                    fileAction.tell(rows, ActorRef.noSender());
//                                    fileAction.tell(rowsKey, ActorRef.noSender());
                                counter += size;
                            }
                            //semaphore.release();
                        } catch (Exception e) {
                            //semaphore.release();
                            log.error(e.getMessage());
                        }
                    }
//                }
            } catch (Exception e) {
                log.error(Throwables.getStackTraceAsString(e));
                //semaphore.release();
            }
        }

    }
}
