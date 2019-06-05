package com.huawei.imbp.admin.service;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.huawei.imbp.admin.entity.InputData;
import com.huawei.imbp.admin.entity.Payload;
import com.huawei.imbp.admin.util.FileUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;


@Component
@Log4j2
public class FileLoadService {

    int count = 0;

    @Autowired
    private WebClient webClient;

    private static final String DIR = "D://data//";
    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");

    public void parse(){
        long startTime = System.currentTimeMillis();
        FileUtil fileUtil = new FileUtil();
        Collection<File> files = fileUtil.getAllFilesMatchFileNameExtension(DIR, "json");

        files.stream().forEach(file -> {
            long startT = System.currentTimeMillis();
            log.info("start process "+file);
            try {
                byte[] data = getFileBytes(file);
                InputStream reader = new ByteArrayInputStream(data);
                processData(reader);
                log.info("finish "+file+" process, took "+((System.currentTimeMillis() - startT))/(1000*60));
            }catch (Exception ex){
                log.error(ex);
            }
        });
        long spend = (System.currentTimeMillis() - startTime)/(1000*60);
        log.info("final insert takes "+spend);
        log.info("number of error data "+count);
    }

    public byte[] getFileBytes(File file) throws Exception{

        Path path = Paths.get(file.getPath());
        byte[] data = Files.readAllBytes(path);
        return data;
    }

    public void processData(InputStream in) throws Exception{
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        reader.beginArray();
        Gson gson = new Gson();
        while(reader.hasNext()){
            Payload payload = gson.fromJson(reader, Payload.class);
            parseData(payload);
        }
    }

    public void parseData(Payload payload) {

        try {
            String fileName = payload.getFile_name();
            String[] files = fileName.split("\\.");
            DateTime time =  dtf.parseDateTime(files[0]);
            long milli = time.getMillis();

            payload.setCreated_time(milli+"");
            payload.setExtension("ext");

            String filePath = DIR+payload.getCreated_day()+"//"+payload.getDevice_type()+"//"+
                payload.getLabel()+"//"+payload.getFile_name();


            File imageFile = new File(filePath);
            byte[] data = getFileBytes(imageFile);
            String image = Base64.encodeBase64String(data);
            payload.setImage(image);
            InputData inputData = new InputData();
            inputData.setPayload(payload);
            webClient.post().uri("http://localhost:8085/api/aoi/etl")
                    .syncBody(inputData)
                    .exchange().subscribe();
            log.debug(payload.getCreated_day());
        }catch (Exception ex){
            log.error(ex);
            count++;
        }

    }
}
