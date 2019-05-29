package com.huawei.imbp.rt.transfer;

import com.huawei.imbp.rt.common.JobStatus;
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
public class ClientData {

    private String ip;
    private JobStatus status;
}
