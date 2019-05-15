package com.huawei.imbp.rt.util;

import com.google.common.base.Throwables;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author Charles(Li) Cai
 * @date 4/12/2019
 */

@Log4j2
@Component
public class OffHeapMemoryAllocation {

    public static Unsafe unsafe;
    public long address;

    public OffHeapMemoryAllocation(){

        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);
        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
        }

    }

    public long getAddress(long size){

        address = unsafe.allocateMemory(size);
        return address;
    }

    public void put(int i, String data){

        try {
            String row = new String();
            unsafe.putObject(row, address + i, data);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
        }
    }

    public String get(int i){

        String row = new String();
        try{

            unsafe.getByte(row,address+i);

        }catch (Exception ex){
            log.error(Throwables.getStackTraceAsString(ex));
        }
        return row;
    }
}
