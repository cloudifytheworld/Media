package com.fw.imbp.rt.entity;

import com.fw.imbp.rt.common.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
