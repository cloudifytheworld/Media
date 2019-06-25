package com.fw.imbp.etl.transform;

import com.fw.imbp.etl.common.ImbpException;
import com.fw.imbp.etl.common.DataType;

/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */

public interface Conversion<T> {

    ImbpException imbpEx = new ImbpException();
    public T convert(DataType sourceType, Object data, String targetFormat, String sourceFormat) throws Exception;
}
