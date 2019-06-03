package com.huawei.imbp.rt.transfer;

import com.huawei.imbp.rt.common.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * @author Charles(Li) Cai
 * @date 5/26/2019
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientData {

    private String serverIp;
    private int serverPort;
    private String clientIp;
    private String clientId;
    private String groupId;
    private String system;
    private String startDate;
    private String endDate;
    private JobStatus status;

}
