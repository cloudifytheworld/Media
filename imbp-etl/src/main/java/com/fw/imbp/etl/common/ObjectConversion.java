package com.fw.imbp.etl.common;

import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Charles(Li) Cai
 * @date 4/5/2019
 */

@Log4j2
public class ObjectConversion {

    public static byte[] toByteArray(Object obj) throws Exception {

        byte[] bytes;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        }catch (Exception e){
            throw e;
        } finally {
            oos.close();
            bos.close();
        }
        return bytes;
    }

    public static Object toObject(byte[] bytes) throws Exception{

        Object obj;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } catch (Exception e){
            throw e;
        }finally {
            bis.close();
            ois.close();
        }
        return obj;
    }
}
