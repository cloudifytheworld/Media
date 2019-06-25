package com.fw.imbp.etl.common;

/**
 * @author Charles(Li) Cai
 * @date 4/24/2019
 */

public enum DataType {

    TEXT("text"),
    BLOB("blob"),
    TIMESTAMP("timestamp"),
    VARCHAR("varchar"),
    BIGINT("bigint"),
    INT("int"),
    BOOLEAN("boolean"),
    DATE("date"),
    DATETIME("datetime"),
    DECIMAL("decimal"),
    DOUBLE("double"),
    FLOAT("float"),
    TIME("time"),
    TIMEUUID("timeuuid"),
    UUID("uuid"),
    SMALLINIT("smallint"),
    TINYINT("tinyint"),
    VARINT("varint"),
    INET("inet"),
    LONG("long"),
    INTEGER("integer"),
    BINARY("binary"),
    COUNTER("counter");

    String type;

    DataType(String type){
        this.type = type;
    }
}
