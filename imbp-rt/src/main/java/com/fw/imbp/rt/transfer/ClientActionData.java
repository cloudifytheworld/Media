package com.fw.imbp.rt.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CountDownLatch;

/**
 * @author Charles(Li) Cai
 * @date 5/31/2019
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientActionData {

    private String system;
    private String start;
    private String end;
    private String groupId;
    private String serverIp;
    private CountDownLatch jobs;
    private DataManager dataManager;
    private DataServer dataServer;
}
