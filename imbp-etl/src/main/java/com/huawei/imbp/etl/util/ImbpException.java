package com.huawei.imbp.etl.util;

/**
 * @author Charles(Li) Cai
 * @date 3/7/2019
 */

public class ImbpException extends Exception {

    private static final String EMPTY = " is empty";
    private static final String MISSING = " is missing";
    private static final String MAPPING = " is not mapped";
    private static final String EMPTY_OR_MISSING_NOMAPPING = " field is either empty, missing or not mapped";

    private String message;

    public ImbpException(){}
    public ImbpException setMessage(String message){
        this.message = message;
        return this;
    }

    @Override
    public String getMessage(){
        return message;
    }

    public ImbpException setEmpty(String key){
        this.message = key+EMPTY;
        return this;
    }

    public ImbpException setMissing(String key){
        this.message = key+MISSING;
        return this;
    }

    public ImbpException setNotMapping(String key){
        this.message = key+MAPPING;
        return this;
    }

    public ImbpException setEmptyOrMissingOrMapping(String key){
        this.message = key+EMPTY_OR_MISSING_NOMAPPING;
        return this;
    }
}
