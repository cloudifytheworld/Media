package com.huawei.imbp.rt.entity;

import com.huawei.imbp.rt.common.JobStatus;
import com.huawei.imbp.rt.service.QueueService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Charles(Li) Cai
 * @date 5/26/2019
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientData<T> {

    private String serverIp;
    private int serverPort;
    private String clientIp;
    private String clientId;
    private String groupId;
    private String system;
    private JobStatus status;
    private String date;
    private long startTime;
    private long endTime;
    private boolean dateTimeRange;
    private boolean consolidation;

}
