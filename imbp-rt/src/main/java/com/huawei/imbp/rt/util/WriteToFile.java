package com.huawei.imbp.rt.util;

import com.datastax.driver.core.Row;
import com.google.common.base.Throwables;
import com.huawei.imbp.rt.entity.Aoi;
import com.huawei.imbp.rt.entity.AoiEntity;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;

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

    public static void writeToFile(List<Row> rows, String key){

        rows.forEach( r -> {
            try {
                Aoi aoi = EntityMappingUtil.mappingAoi(r);
                byte[] bytes = aoi.toString().getBytes();
                long size = bytes.length;
                StatisticManager.total += (double)size/1000000;
                StatisticManager.putBytes(key, size);
                String minuKey = key+":minus-"+aoi.getMinus();
                StatisticManager.putMinus(minuKey, size);
                //Files.write(filePath, bytes, StandardOpenOption.APPEND);
            }catch (Exception e){
                log.error(Throwables.getStackTraceAsString(e));
            }
        });
    }

    public static void writeToFile(List<AoiEntity> aoiEntities){

        StatisticManager.counter += aoiEntities.size();

        aoiEntities.forEach( aoiEntity -> {
            try {
                byte[] bytes = aoiEntity.toString().getBytes();
                long size = bytes.length;
                StatisticManager.total += (double) size / 1000000;
//                StatisticManager.putDay(aoiEntity.getKey().getCreatedDay(), size);
//                String minuKey = aoiEntity.getKey().getCreatedDay()
//                        + ":deviceType-" + aoiEntity.getKey().getDeviceType();
//                StatisticManager.putMinus(minuKey, size);
                Files.write(filePath, bytes, StandardOpenOption.APPEND);
            } catch (Exception e) {
                log.error(Throwables.getStackTraceAsString(e));
            }
        });
    }

    public static void writeToFile(List<Row> rows, String key, String hour, int which){

        StatisticManager.counter += rows.size();

        rows.forEach( r -> {
            try {

//                Aoi aoi = EntityMappingUtil.mappingAoi(r);
//                String image = aoi.getImage();
//
//                byte[] bytes = image.getBytes();
//                if(Base64.isBase64(image)) {
//                    bytes = Base64.decodeBase64(image);
//                }
//                long size = bytes.length;
                long size = r.getBytes("image").array().length;
//                StatisticManager.putDevice(key, size);
//                StatisticManager.putEachHour(key+hour+"-"+which, size);
                StatisticManager.total += (double)size/1000000;
//                StatisticManager.putBytes(key+hour+"-"+which, size);
//                String minuKey = key+hour+"-"+which+":minus-"+aoi.getMinus();
//                StatisticManager.putEachMinus(minuKey, size);
//                StatisticManager.putMinus(minuKey, size);
//                Files.write(filePath, bytes, StandardOpenOption.APPEND);
            }catch (Exception e){
                log.error(Throwables.getStackTraceAsString(e));
            }
        });
    }

//    public static void writeToFile(List<Row> rows){
//
//        rows.forEach( r -> {
//            try {
//                Aoi aoi = EntityMappingUtil.mappingAoi(r);
//                Files.write(filePath, aoi.toString().getBytes(), StandardOpenOption.APPEND);
//            }catch (Exception e){
//                log.error(Throwables.getStackTraceAsString(e));
//            }
//        });
//    }
}
