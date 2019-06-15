package com.huawei.imbp.rt.service;

import lombok.extern.log4j.Log4j2;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Charles(Li) Cai
 * @date 6/2/2019
 */

@Log4j2
public class NetworkManageService {

    int port;

    private final Random random = new Random();
    private final int portMin = 4000;
    private final int portMax = 4100;
    private final InetSocketAddress socketAddress;


    public NetworkManageService(int port) throws Exception{
        this.port = port;
        socketAddress = createSocketAddress();
    }

    public InetSocketAddress getSocketAddress(){
        return socketAddress;
    }

    public InetSocketAddress createSocketAddress() throws Exception{

        InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), findAvailablePort());
        return inetSocketAddress;
    }


    public int getSocketPort(){
        return socketAddress.getPort();
    }

    public String getServerIp(){
        return socketAddress.getAddress().getHostAddress();
    }

    private int nextPort(){
        int portRange = portMax - portMin;
        int port = portMin+random.nextInt(portRange);
        return port;
    }

    private boolean isPortAvailable(int port){

        try{
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.close();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private int findAvailablePort(){

        int portRange = portMax - portMin;
        AtomicInteger count = new AtomicInteger();
        while(count.getAndIncrement()<portRange) {
            int port = nextPort();
            if(isPortAvailable(port)){
                return port;
            }
        }

        throw new IllegalStateException(String.format("Could not find an available port in the range [%d, %d]", portMin, portMax));
    }

    public List<String> getNetworkInterface(){

        List<String> addresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> netInt = NetworkInterface.getNetworkInterfaces();
            Collections.list(netInt).forEach( n -> {
                Enumeration<InetAddress> inetAddresses = n.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    addresses.add(inetAddress.getHostAddress()+":"+port);
                }
            });
            return addresses;
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return null;
    }

}
