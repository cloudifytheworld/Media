package com.huawei.imbp.rt.entity;

import com.huawei.imbp.rt.service.QueueService;
import lombok.Data;

import java.util.concurrent.CountDownLatch;

/**
 * @author Charles(Li) Cai
 * @date 5/17/2019
 */
@Data
public class FeedEntity<T> {

    private QueueService<T>  queue;
    private String system;
    private String date;
    private String deviceType;
    private String hour;
    private CountDownLatch valueLatch;
}
