package com.huawei.imbp.rt.transfer;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */

@Log4j2
public class DataWriter {

    private final FileChannel channel;

    public DataWriter(String filePath, String fileName) throws Exception{

        File dir = new File(filePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(filePath+fileName);
        if(!file.exists()){
            file.createNewFile();
        }
        this.channel = FileChannel.open(Paths.get(file.getPath()), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
    }

    public void write(final ByteBuffer buffer) throws IOException {
        assert !Objects.isNull(buffer);

        int bytesWritten = 0;
        while(buffer.hasRemaining()) {
            bytesWritten += this.channel.write(buffer, bytesWritten);
        }
    }

    public void close() throws Exception{
        this.channel.close();
    }
}
