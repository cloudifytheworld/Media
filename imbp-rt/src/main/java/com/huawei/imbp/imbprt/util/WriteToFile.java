package com.huawei.imbp.imbprt.util;

import com.datastax.driver.core.Row;
import com.google.common.base.Throwables;
import com.huawei.imbp.imbprt.Entity.Aoi;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * @author Charles(Li) Cai
 * @date 4/15/2019
 */

@Log4j2
public class WriteToFile {

    private static Path filePath;

    static{
        try {
            File file = new File("D://test//20181103.txt");
            filePath = Paths.get(file.getPath());
            file.createNewFile();
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public static void writeToFile(ByteBuffer[] byteBuffers, int howMany, List<Integer> index){

        for(int i=0; i<howMany; i++) {
            byteBuffers[i].rewind();
        }

        for(int y = 0; y<index.size(); y++){
            int which = y%howMany;
            int size = index.get(y);
            byte[] bytes = new byte[size];
            byteBuffers[which].get(bytes, 0, size);
            try {
                Files.write(filePath, bytes, StandardOpenOption.APPEND);
            }catch (Exception e){
                log.error(e.getMessage());
            }
        };
    }

    public static void writeToFile(List<Row> rows){

        rows.forEach( r -> {
            try {
                Aoi aoi = EntityMappingUtil.mappingAoi(r);
                Files.write(filePath, aoi.toString().getBytes(), StandardOpenOption.APPEND);
            }catch (Exception e){
                log.error(Throwables.getStackTraceAsString(e));
            }
        });
    }
}
