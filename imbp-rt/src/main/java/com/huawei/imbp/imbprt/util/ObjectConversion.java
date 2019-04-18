package com.huawei.imbp.imbprt.util;

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
        log.debug("convert object to byteArray");
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            log.debug("complete object to byteArray");
        } finally {
            log.debug("starting close obj to byte conversion");
            oos.close();
            bos.close();
            log.debug("complete close object to byteArray");
        }
        return bytes;
    }

    public static Object toObject(byte[] bytes) throws Exception{

        Object obj;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        log.debug("convert byteArray to object");
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            log.debug("complete object to byteArray");
        } finally {
            log.debug("starting close byte to obj conversion");
            bis.close();
            ois.close();
            log.debug("complete close byteArray to object");
        }
        return obj;
    }
}
