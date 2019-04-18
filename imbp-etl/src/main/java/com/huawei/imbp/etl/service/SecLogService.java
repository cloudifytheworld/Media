package com.huawei.imbp.etl.service;

import com.google.common.base.Throwables;
import com.huawei.imbp.etl.entity.ActionEntity;
import com.huawei.imbp.etl.common.ObjectConversion;
import com.huawei.imbp.etl.entity.IndexEntity;
import com.huawei.imbp.etl.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author Charles(Li) Cai
 * @date 4/3/2019
 */

@Component
@RefreshScope
@Log4j2
public class SecLogService {


    @Value("${sec.location}")
    private String secLocation;

    @Value("${sec.indexName}")
    private String indexName;

    @Value("${sec.secLogName}")
    private String secLogName;

    private volatile long offset;

    private Path indexPath;
    private Path secLogPath;

    public final ConcurrentSkipListMap<String, IndexEntity> indexMap = new ConcurrentSkipListMap();

    @PostConstruct
    public void init(){

        try {
            File indexFile = new File(secLocation + indexName);
            indexPath = Paths.get(indexFile.getPath());

            if (!indexFile.exists()) {
                log.info("create sec index file");
                try {
                    indexFile.createNewFile();
                } catch (Exception ex) {
                    log.error("fail to create sec index file "+ex.getMessage());
                }
            } else {
                log.info(indexFile.getName() + " exists");
                loadIndexFile();
                //getSecData();
            }

            File secLogFile = new File(secLocation + secLogName);
            if (!secLogFile.exists()) {
                log.info("create sec log file");
                try {
                    secLogFile.createNewFile();
                } catch (Exception ex) {
                    log.error("fail to create sec log file "+ex.getMessage());
                }
            } else {
                log.info(secLogFile.getName() + " exists");
            }
            secLogPath = Paths.get(secLogFile.getPath());
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
        }
    }

    public void saveRawData(ActionEntity input){

        try {
            Object rawData = input.getInput();
            byte[] data = ObjectConversion.toByteArray(rawData);
            if(saveIndexData(input, data.length)) {
                Files.write(secLogPath, data, StandardOpenOption.APPEND);
            }
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
        }

    }

    public boolean saveIndexData(ActionEntity input, long length){

        try {
            String id = input.getId();
            String msg = input.getErrorMsg();

            IndexEntity dataValue = indexMap.get(id);
            if(dataValue != null) return false;
            dataValue = new IndexEntity();

            Long value = offset << 1 | length << 32;
            offset += length;
            String index = id + ":" + value + ":"+ msg + System.lineSeparator();
            Files.write(indexPath, index.getBytes(), StandardOpenOption.APPEND);
            dataValue.setIndex(value);
            dataValue.setErrorMsg(msg);
            indexMap.put(id, dataValue);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
            return false;
        }

        return true;
    }

    private void loadIndexFile(){

        try {
            List<String> lines = Files.readAllLines(indexPath);
            lines.stream().forEach(s ->{
                IndexEntity dataValue = new IndexEntity();
                String[] line = s.split(":");
                dataValue.setIndex(Long.parseLong(line[1]));
                dataValue.setErrorMsg(line[2]);
                indexMap.put(line[0], dataValue);
            });
            String line = lines.get(lines.size()-1);
            offset = DataUtil.getOffset(Long.parseLong(line.split(":")[1]));
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
        }
    }

    public void getSecData() {

        try {
            RandomAccessFile rand = new RandomAccessFile(secLocation + secLogName, "r");
            indexMap.forEach((k, v) -> {
                try {
                    Long value = v.getIndex();
                    int size = (int) (value >> 32);
                    rand.seek((value & Long.decode("0xFFFFFFFF")) >> 1);
                    byte[] data = new byte[size];
                    rand.read(data, 0, size);
                    Map obj = (Map)ObjectConversion.toObject(data);
                    log.info(obj.get("system"));
                }catch (Exception ex){
                    log.error("failed to load sec data "+k);
                }
            });
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
        }
    }
}
