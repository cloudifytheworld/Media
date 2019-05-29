package com.huawei.imbp.rt.transfer;

import com.datastax.driver.core.Row;
import com.huawei.imbp.rt.common.Constant;
import com.huawei.imbp.rt.entity.Aoi;
import com.huawei.imbp.rt.util.EntityMappingUtil;
import lombok.extern.log4j.Log4j2;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */

@Log4j2
public class DataSender {

    private final AsynchronousSocketChannel sockChannel;
    private final InetSocketAddress inetAddress;
    private final String ipAddress;

    public DataSender(InetSocketAddress inetAddress){

        this.inetAddress = inetAddress;
        try {
            sockChannel = AsynchronousSocketChannel.open();
            Future<Void> result = sockChannel.connect(inetAddress);
            result.get();
            ipAddress = InetAddress.getLocalHost().getHostAddress();
            log.info(inetAddress+ " is connected");
        }catch (Exception e){
            throw new IllegalStateException("unable to start DataSender", e);
        }
    }

    public void write(Row data){

        Aoi aoi = EntityMappingUtil.mappingAoi(data);
        ByteBuffer buffer = ByteBuffer.wrap(aoi.toString().getBytes());
        sockChannel.write(buffer);
        buffer.clear();
    }

    public void close(){

        try {
            ByteBuffer buffer = ByteBuffer.wrap(Constant.END_MARKER.getBytes());
            sockChannel.write(buffer);
            sockChannel.close();
        }catch (Exception e){
            log.error(ipAddress+" fail to close client channel "+e.getMessage());
        }
    }
}
