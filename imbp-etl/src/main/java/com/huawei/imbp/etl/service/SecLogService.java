package com.huawei.imbp.etl.service;

import com.google.common.base.Throwables;
import com.huawei.imbp.etl.common.ImbpCommon;
import com.huawei.imbp.etl.common.ImbpException;
import com.huawei.imbp.etl.entity.ActionEntity;
import com.huawei.imbp.etl.common.ObjectConversion;
import com.huawei.imbp.etl.entity.IndexEntity;
import com.huawei.imbp.etl.entity.IndexResult;
import com.huawei.imbp.etl.entity.ResultEntity;
import com.huawei.imbp.etl.util.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author Charles(Li) Cai
 * @date 4/3/2019
 */

@Component
@RefreshScope
@Log4j2
public class SecLogService {

    //Todo Refactor SeclogService
    
    @Value("${sec.location}")
    private String secLocation;

    @Value("${sec.indexName}")
    private String indexName;

    @Value("${sec.secLogName}")
    private String secLogName;

    @Autowired
    LoggingService loggingService;

    private volatile long offset;

    private Path indexPath;
    private Path secLogPath;

    private ConcurrentSkipListMap<String, IndexEntity> indexMap = new ConcurrentSkipListMap();

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
            String system = input.getSystem();

            IndexEntity dataValue = indexMap.get(id);
            if(dataValue != null) {
                long index = dataValue.getIndex();
                if((index & 1) == 1){
                    reset(id, true);
                }
                return false;
            }
            dataValue = new IndexEntity();
            //Todo add system to index file
            Long value = createIndexData(msg, id, system, length);
            dataValue.setIndex(value);
            dataValue.setErrorMsg(msg);
            dataValue.setSystem(system);
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
            if(lines !=null && lines.size() > 0) {
                lines.stream().forEach(s -> {
                    IndexEntity dataValue = new IndexEntity();
                    String[] line = s.split(":");
                    dataValue.setIndex(Long.parseLong(line[1]));
                    dataValue.setSystem(line[2]);
                    dataValue.setErrorMsg(line[3]);
                    indexMap.put(line[0], dataValue);
                });
                String line = lines.get(lines.size() - 1);
                offset = DataUtil.getOffset(Long.parseLong(line.split(":")[1]));
            }
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
        }
    }

    public List<ActionEntity>  getSecData() {

        List<ActionEntity> actionEntities = new ArrayList<>();

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
                    setSecEntity(actionEntities, obj, k, v.getSystem(), v.getErrorMsg());
                }catch (Exception ex){
                    log.error("failed to load "+k+" sec data "+Throwables.getStackTraceAsString(ex));
                }
            });
            rand.close();
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
        }
        return actionEntities;
    }

    public List<IndexResult> getIndexMap() {

        List<IndexResult> results = new ArrayList<>();
        for(String key : indexMap.keySet()){
            IndexEntity indexEntity = indexMap.get(key);
            long index = indexEntity.getIndex();
            if((index & 1) == 1) continue;
            IndexResult result = new IndexResult();
            result.setId(key);
            result.setErrorMsg(indexEntity.getErrorMsg());
            results.add(result);
        };
        return results;
    }

    public Map<String, Integer> getSize() {
        int tombstone = 0;
        int size = 0;
        Map<String, Integer> count = new HashMap<>();
        for(String key : indexMap.keySet()) {
            IndexEntity indexEntity = indexMap.get(key);
            long index = indexEntity.getIndex();
            if((index & 1) == 1){
                tombstone++;
                continue;
            }
            size++;
        }
        count.put("tombstone", tombstone);
        count.put("size", size);
        return count;
    }

    public List<ActionEntity> getDataById(String[] ids){

        List<ActionEntity> actionEntities = new ArrayList<>();

        try {
            RandomAccessFile rand = new RandomAccessFile(secLocation + secLogName, "r");
            Arrays.stream(ids).forEach(id -> {
                IndexEntity indexEntity = indexMap.get(id);
                try {
                    if(indexEntity == null) throw new ImbpException().setMessage("NO SUCH ID");
                    Map<String, Object> obj = getSecMapData(indexEntity, rand);
                    setSecEntity(actionEntities, obj, id, indexEntity.getSystem(), indexEntity.getErrorMsg());
                }catch (Exception e){
                    log.debug("failed to load "+id+" from sec data "+Throwables.getStackTraceAsString(e));
                    setSecEntity(actionEntities, null, id, null, "failed to load "+id+" from sec data "+e.getMessage());
                }
            });
            rand.close();
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
            setSecEntity(actionEntities, null, null, null,"failed to load sec data "+ex.getMessage());
        }

        return actionEntities;
    }

    public ResultEntity updateDataById(ActionEntity actionEntity){

        String id;
        ResultEntity resultEntity = new ResultEntity();

        try {
            String inputId = actionEntity.getId();
            IndexEntity oldIndexEntity = indexMap.get(inputId);
            if(oldIndexEntity != null){
                Long index = oldIndexEntity.getIndex();
                if((index & 1) == 1){
                    resultEntity.setStatus(ImbpCommon.FAIL);
                    resultEntity.setMessage(inputId+" is already updated and deleted");
                    return resultEntity;
                }
            }else{
                resultEntity.setStatus(ImbpCommon.FAIL);
                resultEntity.setMessage(inputId+" doesn't exists");
                return resultEntity;
            }

            Map<String, Object> payload = actionEntity.getInput();
            String system = actionEntity.getSystem();
            if(payload == null || payload.size() == 0){
                resultEntity.setStatus(ImbpCommon.FAIL);
                resultEntity.setMessage(inputId+" is empty");
                return resultEntity;
            }
            id = DataUtil.createId(payload);
            IndexEntity newIndexEntity = indexMap.get(id);
            if (newIndexEntity != null) {
                resultEntity.setStatus(ImbpCommon.FAIL);
                resultEntity.setMessage(inputId+" is "+ImbpCommon.DUPLICATE);
                return resultEntity;
            }
            newIndexEntity = new IndexEntity();
            byte[] data = ObjectConversion.toByteArray(payload);
            int size = data.length;
            String errorMsg = actionEntity.getErrorMsg()+"--"+ImbpCommon.UPDATED;
            long value = createIndexData(errorMsg, id, system, size);
            Files.write(secLogPath, data, StandardOpenOption.APPEND);
            newIndexEntity.setIndex(value);
            newIndexEntity.setErrorMsg(errorMsg);
            newIndexEntity.setSystem(system);
            reset(actionEntity.getId(), false);
            indexMap.put(id, newIndexEntity);
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
            resultEntity.setStatus(ImbpCommon.FAIL);
            resultEntity.setMessage(ex.getMessage());
            return resultEntity;
        }

        resultEntity.setStatus(ImbpCommon.SUCCESS);
        resultEntity.setId(id);
        return resultEntity;


    }

    public ResultEntity deleteDataById(String[] ids){

        ResultEntity resultEntity = new ResultEntity();
        List<String> errorList = new ArrayList<>();

        for(int i=0; i<ids.length; i++) {
            try{
                reset(ids[i], false);
            }catch (Exception ex){
                errorList.add(ids[i]);
                resultEntity.setMessage("fail to delete "+ex.getMessage());
            }
        }

        if(errorList.size() > 0){
            resultEntity.setStatus(ImbpCommon.FAIL);
        }else{
            resultEntity.setStatus(ImbpCommon.SUCCESS);
        }

        return resultEntity;

    }

    public ResultEntity deleteAllData(){

        ResultEntity resultEntity = new ResultEntity();
        try {
            indexMap = new ConcurrentSkipListMap<>();
            Files.newBufferedWriter(indexPath).write("");
            Files.newBufferedWriter(secLogPath).write("");
            offset = 0;
            resultEntity.setStatus(ImbpCommon.SUCCESS);
            return resultEntity;
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
            resultEntity.setStatus(ImbpCommon.FAIL);
            resultEntity.setMessage("fail to delete "+ex.getMessage());
            return resultEntity;
        }
    }

    public ResultEntity replayDataById(String[] ids){

        List<Map<String, Object>> lists = new ArrayList<>();
        List<String> tombStones = new ArrayList<>();
        ResultEntity resultEntity = new ResultEntity();

        try{
            RandomAccessFile rand = new RandomAccessFile(secLocation + secLogName, "r");
            Arrays.stream(ids).forEach(k ->{
                try {
                    IndexEntity indexEntity = indexMap.get(k);
                    if(indexEntity == null) throw new ImbpException().setMessage("NO SUCH ID");
                    Long index = indexEntity.getIndex();
                    if((index & 1) != 1) {
                        Map<String, Object> obj = getSecMapData(indexEntity, rand);
                        //Todo Temp solution
                        obj.put("system",indexEntity.getSystem());
                        lists.add(obj);
                        reset(k, false);
                    }else{
                        resultEntity.setMessage("contains tombstone id");
                        tombStones.add(k);
                    }
                }catch (Exception ex){
                    resultEntity.setStatus(ImbpCommon.FAIL);
                    resultEntity.setMessage("fail to retrieve id(s): "+ex.getMessage());
                    tombStones.add(k);
                    log.error(k+" can't be retrieved");
                }
            });
            rand.close();
            if(lists.size() > 0) {
                loggingService.onRun(lists);
                resultEntity.setStatus(ImbpCommon.SUCCESS);
            }

        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
            resultEntity.setStatus(ImbpCommon.FAIL);
            resultEntity.setMessage("fail to replay the id(s) "+ex.getMessage());
            resultEntity.setIds(tombStones);
            return resultEntity;
        }

        if(tombStones.size() > 0) {
            resultEntity.setIds(tombStones);
        }
        return resultEntity;
    }

    public ResultEntity replayAllData(){

        List<Map<String, Object>> data = new ArrayList<>();
        List<String> error = new ArrayList<>();
        ResultEntity resultEntity = new ResultEntity();

        try {
            RandomAccessFile rand = new RandomAccessFile(secLocation + secLogName, "r");
            for(String key : indexMap.keySet()){
                try {
                    IndexEntity indexEntity = indexMap.get(key);
                    Long index = indexEntity.getIndex();
                    if ((index & 1) != 1) {
                        Map<String, Object> obj = getSecMapData(indexEntity, rand);
                        //Todo Temp solution
                        obj.put("system",indexEntity.getSystem());
                        data.add(obj);
                    }
                }catch (Exception e){
                    resultEntity.setMessage("fail to replay following id(s)");
                    error.add(key);
                }
            }
            if(data.size() > 0) {
                deleteAllData();
                loggingService.onRun(data);
            }
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
            resultEntity.setMessage("fail to run "+ex.getMessage());
            resultEntity.setIds(error);
        }

        resultEntity.setStatus(ImbpCommon.SUCCESS);
        if(error.size()>0) {
            resultEntity.setIds(error);
        }
        return resultEntity;
    }

    private long createIndexData(String msg, String id, String system, long size) throws Exception{

        long value = offset << 1 | size << 32;
        offset += size;
        String index = id+":"+value+":"+system+":"+msg+System.lineSeparator();
        Files.write(indexPath, index.getBytes(), StandardOpenOption.APPEND);
        return value;
    }

    private void reset(String id, boolean alive) throws Exception{

        try{
            IndexEntity indexEntity = indexMap.get(id);
            if(indexEntity == null) return;
            long index = indexEntity.getIndex();
            long nx = alive ? index ^ 1 : index | 1;
            indexEntity.setIndex(nx);
            String value = id+":"+nx+":"+indexEntity.getErrorMsg()+System.lineSeparator();
            RandomAccessFile rand = new RandomAccessFile(secLocation+indexName, "rw");
            String line;
            while((line = rand.readLine()) != null){
                if(line.contains(id+":"+index)){
                    rand.seek(rand.getFilePointer()-line.getBytes().length-System.lineSeparator().getBytes().length);
                    rand.writeBytes(value);break;
                }
            }
            rand.close();
            indexMap.put(id, indexEntity);
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
            throw new Exception("issue with update index file, error: "+ex.getMessage());
        }
    }


    private void setSecEntity(List<ActionEntity> actionEntities, Map<String, Object> obj, String id, String system, String errorMsg){

        ActionEntity actionEntity = new ActionEntity();
        actionEntity.setId(id);
        actionEntity.setInput(obj);
        actionEntity.setSystem(system);
        actionEntity.setErrorMsg(errorMsg);
        actionEntities.add(actionEntity);
    }

    private Map<String, Object> getSecMapData(IndexEntity indexEntity , RandomAccessFile rand) throws Exception{

        Map obj;
        Long value = indexEntity.getIndex();
        int size = (int) (value >> 32);
        Long b = value & Long.decode("0xFFFFFFFF");
        long pos = (b) >> 1;
        rand.seek(pos);
        byte[] data = new byte[size];
        rand.read(data, 0, size);
        try {
            obj = (Map) ObjectConversion.toObject(data);
        }catch (Exception e){
            throw new ImbpException().setMessage("byte to object conversion");
        }
        return obj;
    }
}
