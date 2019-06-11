package com.huawei.imbp.rt.transfer;

import com.google.common.base.Throwables;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */

@Log4j2
@RefreshScope
public class DataWriter {

    @Value("${data.inMemoryWrite}")
    public boolean inMemoryWrite;

    private final FileChannel channel;
    private Path path;

    public DataWriter(String filePath, String fileName){

        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            path = Paths.get(file.getPath());
            this.channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void write(final ByteBuffer buffer) throws IOException {

        assert !Objects.isNull(buffer);
        buffer.flip();
        this.channel.write(buffer);

        if(!inMemoryWrite) {
            this.channel.force(true);
        }
    }

    public void writeToFile(final ByteBuffer buffer) throws Exception{

        while(buffer.hasRemaining()) {
            this.channel.write(buffer);
        }
    }

    public void writeToFile(byte[] aoi){

        try{
            Files.write(path, aoi, StandardOpenOption.APPEND);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
        }
    }

    public void close() throws Exception{
        this.channel.close();
    }
}
