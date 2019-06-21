package com.huawei.imbp.rt.transfer;

import com.google.common.base.Throwables;
import com.huawei.imbp.rt.common.Constant;
import com.huawei.imbp.rt.common.ImbpException;
import lombok.extern.log4j.Log4j2;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
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
                    //Todo client sends processing status in a designate time interval
                    //save it jobStore with timestamp, if timestamp is two to three times larger
                    //than current time and status is still processing, we can safely assuming that
                    //the client is experiencing some kind of hardship
                    if (data.contains(Constant.END_MARKER)) {
                        onComplete.onComplete(data);
                        throw new ImbpException().setMessage("Done read from "+data);
                    }
                    dataWriter.write(buf);
                    buf.flip();
                    read(channel, onComplete);

                }catch (ImbpException imbp) {
                    log.info(imbp.getMessage());
                }catch (ClosedChannelException cx){
                }catch (Exception ex){
                    log.error(Throwables.getStackTraceAsString(ex));
                }
            }


            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                //log.error("reader failed -- "+Throwables.getStackTraceAsString(exc));
            }


        });

    }

}
