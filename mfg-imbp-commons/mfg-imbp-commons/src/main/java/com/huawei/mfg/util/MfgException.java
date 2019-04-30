package com.huawei.mfg.util;

public class MfgException extends BaseMfgException {
    public MfgException() {
    }

    public MfgException(String message) {
        super(message);
    }

    public MfgException(String message, Throwable cause) {
        super(message, cause);
    }
}
