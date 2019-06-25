package com.fw.imbp.rt.transfer;

import lombok.extern.log4j.Log4j2;

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
public class DataWriter {

    public boolean inMemoryWrite;

    private final FileChannel channel;
    private Path path;

    public DataWriter(String filePath, String fileName, boolean inMemoryWrite){

        this.inMemoryWrite = inMemoryWrite;

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
            log.error(e);
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
        buffer.rewind();
    }

    public void writeToFile(final ByteBuffer buffer) throws Exception{

        buffer.flip();
        while(buffer.hasRemaining()) {
            this.channel.write(buffer);
        }
        buffer.rewind();
    }

    public void writeToFile(byte[] aoi){

        try{
            Files.write(path, aoi, StandardOpenOption.APPEND);
        }catch (Exception e){
            log.error(e);
        }
    }

    public void close() throws Exception{
        this.channel.force(true);
        this.channel.close();
    }
}
