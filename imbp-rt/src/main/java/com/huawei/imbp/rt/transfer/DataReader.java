package com.huawei.imbp.rt.transfer;

import com.google.common.base.Throwables;
import com.huawei.imbp.rt.common.Constant;
import lombok.extern.log4j.Log4j2;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */

@Log4j2
public class DataReader {

    DataWriter dataWriter;

    public DataReader(DataWriter dataWriter){
        this.dataWriter = dataWriter;
    }

    public void read(AsynchronousSocketChannel channel, OnComplete onComplete){

        final ByteBuffer buf = ByteBuffer.allocate(4096);
        channel.read(buf, channel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                buf.flip();
                try {
                    if(result > 0) {
                        String data = new String(buf.array().toString());
                        if(data.contains(Constant.END_MARKER)){
                            onComplete.onComplete();
                            channel.close();
                            dataWriter.close();
                        }
                        dataWriter.write(buf);
                    }else{
                        buf.clear();
                        channel.read(buf, channel, this);
                    }
                }catch (Exception e){
                    log.error(Throwables.getStackTraceAsString(e));
                }

            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                log.error(Throwables.getStackTraceAsString(exc));
            }
        });
    }
}
