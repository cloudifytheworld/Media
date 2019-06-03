package com.huawei.imbp.rt.transfer;

import com.google.common.base.Throwables;
import com.huawei.imbp.rt.common.Constant;
import com.huawei.imbp.rt.common.ImbpException;
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

                try {
                    String data = new String(buf.array());
                    if (result < 0 || data.contains(Constant.END_MARKER)) {
                        onComplete.onComplete();
                        dataWriter.close();
                        channel.shutdownInput();
                        channel.shutdownOutput();
                        channel.close();
                        throw new ImbpException().setMessage("Done read from "+data);
                    }
                    dataWriter.write(buf);
                    buf.flip();
                    read(channel, onComplete);

                }catch (ImbpException imbp){
                    log.info(imbp.getMessage());
                }catch (Exception e){
                    onComplete.onComplete();
                    log.error("read failed -- "+Throwables.getStackTraceAsString(e));
                }
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                onComplete.onComplete();
                log.error("reader failed -- "+Throwables.getStackTraceAsString(exc));
            }
        });
    }


}
