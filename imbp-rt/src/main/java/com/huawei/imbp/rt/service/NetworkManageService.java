package com.huawei.imbp.rt.service;

import com.huawei.imbp.rt.common.ImbpException;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author Charles(Li) Cai
 * @date 6/2/2019
 */
public class NetworkManageService {

    ImbpException imbp = new ImbpException();
    private int port;
    private InetAddress inetAddress;
    private InetSocketAddress socketAddress;

    public InetAddress getInetAddress() throws Exception{

        try {
            this.inetAddress = InetAddress.getLocalHost();
            return inetAddress;
        }catch (Exception e){
            throw imbp.setMessage(e.getMessage());
        }
    }


    public InetSocketAddress getSocketAddress(){

        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, 9500);
        return inetSocketAddress;
    }


    public int getSocketPort(){

        return getSocketAddress().getPort();
    }

    public String getServerIp() throws Exception{

        return getInetAddress().getHostAddress();
    }
}
