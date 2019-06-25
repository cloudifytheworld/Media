package com.fw.imbp.etl.service;

import com.fw.imbp.etl.common.ImbpException;

/**
 * @author Charles(Li) Cai
 * @date 4/24/2019
 */
public interface DbService {

    ImbpException imbpEx = new ImbpException();
    enum dbDirection{
        SOURCE, TARGET
    }
}
