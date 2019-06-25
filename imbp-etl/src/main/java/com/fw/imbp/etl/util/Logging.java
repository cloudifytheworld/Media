package com.fw.imbp.etl.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 3/21/2019
 */


public class Logging {

    public static final String packageName = "com.fw.imbp.etl";

    private LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    private Logger logger = loggerContext.getLogger(packageName);

    public void setLogLevel(Level logLevel) {
        logger.setLevel(logLevel);
    }

     public void debug(String msg){
        logger.debug(msg);
    }
}
