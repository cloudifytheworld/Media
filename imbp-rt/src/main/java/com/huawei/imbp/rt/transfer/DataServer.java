package com.huawei.imbp.rt.transfer;

import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */

@Log4j2
public class DataServer {

    private OnComplete onComplete;
    private OnCloseChannel onCloseChannel;

    private final AsynchronousServerSocketChannel server;
    private final AsynchronousChannelGroup serverGroup;
    private final AtomicInteger jobNumber = new AtomicInteger();
    private final DataReader dataReader;
    private DataWriter dataWriter;

    public DataServer(InetSocketAddress inetAddress, int poolSize, String filePath, String groupId, boolean inMemoryWrite){

        try {
            dataWriter = new DataWriter(filePath, groupId, inMemoryWrite);
            this.dataReader = new DataReader(dataWriter);
            this.serverGroup = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(poolSize));
            this.server = AsynchronousServerSocketChannel.open(this.serverGroup).setOption(
                    StandardSocketOptions.SO_REUSEADDR, true).bind(inetAddress);
            log.info(String.format("server address %s:%d ", inetAddress.getAddress(), inetAddress.getPort()));
        } catch (Exception e) {
            throw new IllegalStateException("unable to start DataServer", e);
        }
    }

    public DataServer run(CountDownLatch ready){

        this.server.accept(server, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel server) {

                server.accept(server, this);
                log.info("process connection #"+jobNumber.getAndIncrement());
                dataReader.read(channel, onComplete);

                onClose( () -> {
                    try {
                        channel.shutdownInput();
                        channel.shutdownOutput();
                        channel.close();
                        dataWriter.close();
                    }catch (Exception e){
                        log.error("Closing channel "+e.getMessage());
                    }
                });
            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
                //log.error(this.getClass()+"--"+Throwables.getStackTraceAsString(exc));
            }
        });

        ready.countDown();
        return this;
    }

    public void start(OnComplete onComplete){
        this.onComplete = onComplete;
    }

    public void close(){
        try {
            this.onCloseChannel.onClose();
//            this.serverGroup.shutdown();
//            this.serverGroup.awaitTermination(1000, TimeUnit.MILLISECONDS);
            this.server.close();
        }catch (Exception e){
            log.error("unable to shut down "+e.getMessage());
        }
    }
    public void onClose(OnCloseChannel closeChannel){
        this.onCloseChannel = closeChannel;
    }
}
