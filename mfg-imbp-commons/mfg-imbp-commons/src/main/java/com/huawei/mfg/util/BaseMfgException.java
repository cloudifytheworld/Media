package com.huawei.mfg.util;

public class BaseMfgException extends Exception{
    public BaseMfgException() {
    }

    public BaseMfgException(String message) {
        super(message);
    }

    public BaseMfgException(String message, Throwable cause) {
        super(message, cause);
    }

}
