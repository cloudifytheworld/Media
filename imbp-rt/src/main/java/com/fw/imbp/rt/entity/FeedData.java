package com.fw.imbp.rt.entity;

import com.fw.imbp.rt.service.QueueService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Charles(Li) Cai
 * @date 6/13/2019
 */

@Data
@NoArgsConstructor
@ToString
public class FeedData<T> {

    private CountDownLatch valueLatch;
    private QueueService<T> queue;
    private List<ClientDateTime> dateTimes;
    private boolean dateTimeRange;
    private String system;

}
