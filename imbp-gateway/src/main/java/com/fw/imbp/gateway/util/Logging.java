package com.fw.imbp.gateway.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

/**
 * @author Charles(Li) Cai
 * @date 3/18/2019
 */

public class Logging {

    private String packageName = "com.fw.imbp.gateway";
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

    public void info(String msg){
        logger.info(msg);
    }
}
