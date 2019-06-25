package com.fw.imbp.rt.transfer;

import lombok.extern.log4j.Log4j2;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */

@Log4j2
public class DataClient {

    private final AsynchronousSocketChannel sockChannel;
    private final String ipAddress;

    public DataClient(InetSocketAddress inetAddress){

        try {
            sockChannel = AsynchronousSocketChannel.open();
            sockChannel.connect(inetAddress).get();
            ipAddress = InetAddress.getLocalHost().getHostAddress();
            log.info(inetAddress+ " is connected");
        }catch (Exception e){
            throw new IllegalStateException("unable to start DataClient", e);
        }
    }

    public void write(ByteBuffer data){

        try {
            sockChannel.write(data).get();
        }catch (Exception e){
            log.error(e);
        }
    }

    public void close(){

        try {
            sockChannel.shutdownOutput();
            sockChannel.close();
        }catch (Exception e){
            log.error(ipAddress+" fail to close client channel "+e.getMessage());
        }
    }
}