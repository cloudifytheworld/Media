package com.huawei.imbp.rt.transfer;

import com.google.common.base.Throwables;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */

@Log4j2
public class DataReceiver {

    private OnComplete onComplete;
    private final AsynchronousServerSocketChannel server;
    private final AsynchronousChannelGroup serverGroup;
    private final String groupId;
    private final AtomicInteger jobNumber = new AtomicInteger();
    private final DataReader dataReader;
    public String getGroupId() {
        return groupId;
    }

    public DataReceiver(InetSocketAddress inetAddress, int poolSize, String filePath){

        groupId = UUID.randomUUID().toString();

        try {
            DataWriter dataWriter = new DataWriter(filePath, groupId);
            this.dataReader = new DataReader(dataWriter);
            this.serverGroup = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(poolSize));
            this.server = AsynchronousServerSocketChannel.open(this.serverGroup).bind(inetAddress);
        } catch (Exception e) {
            throw new IllegalStateException("unable to start DataReceiver", e);
        }
    }

    public void run(CountDownLatch jobs){//, CountDownLatch ready){

        this.server.accept(server, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel server) {
                //server.accept(server, this);
                server.accept();
                log.info("process connection #"+jobNumber.getAndIncrement());
                dataReader.read(channel, onComplete);
            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
                log.error(Throwables.getStackTraceAsString(exc));
            }
        });

//        ready.countDown();
        start(() -> {
            jobs.countDown();
        });

    }

    private void start(OnComplete onComplete){
        this.onComplete = onComplete;
    }

    public void close(){
        try {
            this.server.close();
            this.serverGroup.shutdown();
            this.serverGroup.awaitTermination(10000, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            log.error("unable to shut down "+e.getMessage());
        }
    }
}
