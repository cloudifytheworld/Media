package com.fw.imbp.etl.transform;

import com.fw.imbp.etl.common.DataType;
import com.fw.imbp.etl.common.ImbpException;


/**
 * @author Charles(Li) Cai
 * @date 4/25/2019
 */


public class ManageConversion {

    private LongConversion longConversion;
    private TextConversion textConversion;
    private BlobConversion blobConversion;
    private DateConversion dateConversion;
    private DoubleConversion doubleConversion;
    private TimestampConversion timestampConversion;
    private BooleanConversion booleanConversion;
    private IntConversion intConversion;

    public ManageConversion(){
        longConversion = new LongConversion();
        textConversion = new TextConversion();
        blobConversion = new BlobConversion();
        dateConversion = new DateConversion();
        doubleConversion = new DoubleConversion();
        timestampConversion = new TimestampConversion();
        booleanConversion = new BooleanConversion();
        intConversion = new IntConversion();
    }

    public Conversion getConversion(DataType targetType) throws Exception{

        switch (targetType){
            case LONG:
                return longConversion;
            case TEXT: case VARCHAR:
                return textConversion;
            case BLOB:
                return blobConversion;
            case DATE:
                return dateConversion;
            case DECIMAL: case DOUBLE: case FLOAT:
                return doubleConversion;
            case TIMESTAMP:
                return timestampConversion;
            case BOOLEAN:
                return booleanConversion;
            case INTEGER:case INT:
                return intConversion;
            default:
                throw new ImbpException().setMessage(targetType.name()+" conversion is not supported");
        }

    }
}
