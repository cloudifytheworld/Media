package com.huawei.imbp.rt.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * @author Charles(Li) Cai
 * @date 5/30/2019
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerActionData {

    private CountDownLatch jobs;
    private CountDownLatch ready;
    private DataManager dataManager;
    private InetSocketAddress socketAddress;
    private int participatedClients;
    private String filePath;
}
