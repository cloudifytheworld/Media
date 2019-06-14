package com.huawei.imbp.rt.entity;

import com.huawei.imbp.rt.service.QueueService;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Charles(Li) Cai
 * @date 6/13/2019
 */

@Data
@NoArgsConstructor
public class FeedData<T> {

    private CountDownLatch valueLatch;
    private QueueService<T> queue;
    private List<ClientDateTime> dateTimes;
    private boolean dateTimeRange;
    private String system;

}
