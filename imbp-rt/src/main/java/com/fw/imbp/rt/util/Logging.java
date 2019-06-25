package com.fw.imbp.rt.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

/**
 * @author Charles(Li) Cai
 * @date 04/08/2019
 */


public class Logging {

    public static final String packageName = "com.fw.imbp.rt";

    private LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    private Logger logger = loggerContext.getLogger(packageName);

    public void setLogLevel(Level logLevel) {
        logger.setLevel(logLevel);
    }

    public void debug(String msg){
        logger.debug(msg);
    }

    public void error(String msg){
        logger.error(msg);
    }
}
