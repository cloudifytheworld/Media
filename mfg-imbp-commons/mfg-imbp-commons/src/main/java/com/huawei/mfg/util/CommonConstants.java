package com.huawei.mfg.util;

import com.google.common.base.Strings;


public interface CommonConstants {
    public static final String MARIA_JDBC_DATASOURCE_CLASSNAME = "org.mariadb.jdbc.MariaDbDataSource";
    public static final String DEFAULT_MYSQL_CONNECTION_TEST_QUERY = "SELECT 1";
    //config.setProperties("useUnicode=true;characterEncoding=utf8&autoReconnect=true");
    public static final String MYSQL_CONNECTION_OPTIONS = "useUnicode=true;characterEncoding=utf8&autoReconnect=true";
    public static final int DEFAUL_MYSQL_SERVER_PORT = 3306;
    public static final String JDBC_POOL_NAME = "jdbc.pool.name";
    public static final String JDBC_DB_USERNAME = "jdbc.db.username";
    public static final String JDBC_DB_PASSWORD = "jdbc.db.password";
    public static final String JDBC_DB_DATASOURCE_CLASS = "jdbc.datasource.full.classname";
    public static final String JDBC_MYSQL_TEST_QUERY = "jdbc.mysql.test.query";
    public static final String JDBC_DB_HOST = "jdbc.db.host";
    public static final String JDBC_DB_PORT = "jdbc.db.port";
    public static final String JDBC_DB_NAME = "jdbc.db.name";
    public static final String JDBC_DB_PROPERTIES = "jdbc.db.properties";
    public static final String JDBC_DB_TABLE = "jdbc.db.table";
    public static final String ORACLE_SERVICE_NAME = "jdbc.db.service.name";
    public static final String ORACLE_SID = "jdbc.db.sid";
    public static final String ORACLE_CONNECT_STRING_BY_SERVICE_NAME = "(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=%s)(PORT=%d)))(CONNECT_DATA=(SERVICE_NAME=%s)))";

    public static final String REDIS_CLUSTER_MODE = "redis.cluster.mode";
    public static final String REDIS_CLUSTER_NODES = "redis.cluster.nodes";
    public static final String REDIS_SINGLE_NODE_HOST = "redis.single.node.host";
    public static final String REDIS_SINGLE_NODE_PORT = "redis.single.node.port";
    public static final String REDIS_CLUSTER_HOST_PREFIX = "redis.cluster.host";
    public static final String REDIS_CLUSTER_PORT_PREFIX = "redis.cluster.port";

    public static final String MFG_TIMESTAMP_NOW_KEY = "mfg.ts.now";
    public static final String MFG_DEFAULT_HBASE_COLUMN_FAMILY = "content";

    public static final String METADATA_DB_PREFIX      = "metadata";
    public static final String MYSQL_DB_SOURCE_PREFIX  = "mysql";
    public static final String ORACLE_DB_SOURCE_PREFIX = "oracle";
    public static final String REDIS_SOURCE_PREFIX     = "redis";
    public static final String MYSQL_DB_SOURCE         = "mysql";
    public static final String ORACLE_DB_SOURCE        = "oracle";
    public static final String REDIS_SOURCE            = "redis";

    public static final String NORMALIZE_TABLE_NAME = "normalize.table.name";

    public static final String MFG_MAP_KEY_MAP_ATTR   = "mfg_mkey";
    public static final String MFG_MAP_VALUE_MAP_ATTR = "mfg_value";
    public static final String MFG_ORIG_MAP_MAP_ATTR  = "mfg_omap";
    public static final String MFG_ORIG_ZSET_MAP_ATTR = "mfg_ozset";
    public static final String MFG_ORIG_SET_MAP_ATTR  = "mfg_oset";
    public static final String MFG_ORIG_LIST_MAP_ATTR = "mfg_olist";

    public static final String MFG_REDIS_SINGLE_MODE  = "single";
    public static final String MFG_REDIS_CLUSTER_MODE = "cluster";

    public static final String MFG_SYSTEM_NAME  = "system.name";
    public static final String MFG_FACTORY_NAME  = "factory.name";
    public static final String MFG_EQUIPMENT_NAME  = "equipment.name";
    public static final String MFG_TARGET_NAME  = "target.name";
    public static final String MFG_DB_SOURCE    = "db.source";

    public static final String[] DEFAULT_CREATION_DATE_COLUMNS = {
            "create_date",
            "created",
            "creation_date",
            "source_creation_date",
            "updated",
            "updated_date",
            "update_date",
            "last_updated",
            "last_update_date",
            "last_updated_date"
    };

    public static final String[] POSSIBLE_CREATION_DATE_COLUMNS = {
            "create_date",
            "created",
            "creation_date",
            "source_creation_date",
    };

    public static final String[] POSSIBLE_UPDATE_DATE_COLUMNS = {
            "updated",
            "updated_date",
            "update_date",
            "last_updated",
            "last_update_date",
            "last_updated_date"
    };

    public static final String RESTFUL_API_VERSION_1 = "v1";
    public static final String RESTFUL_API_PUBLISH_PATH = "publish";

    enum SQLDatabaseType {
        MYSQL(),
        ORACLE,
        NONE;
        public static SQLDatabaseType toSQLDatabaseType(String dbName) {
            if (Strings.isNullOrEmpty(dbName)) {
                return NONE;
            }
            else if (dbName.equalsIgnoreCase(MYSQL.name())) {
                return MYSQL;
            }
            else if (dbName.equalsIgnoreCase(ORACLE.name())) {
                return ORACLE;
            }
            else {
                return NONE;
            }
        }
    }

    enum Status {
        OK("ok"),
        FAIL("fail");
        private String type;

        Status(String type) {
            this.type = type;
        }

        public String type() {
            return type;
        }
    }

    enum Target {
        CASSANDRA("cassandra"),
        KAFKA("kafka"),
        HBASE("hbase"),
        HDFS("hdfs"),
        SQL("sql"),
        NONE("hdfs");
        private String type;

        Target(String type) {
            this.type = type;
        }

        public String type() {
            return type;
        }

        public static Target toTarget(String destination) {
            if (Strings.isNullOrEmpty(destination)) {
                return NONE;
            }
            else if (destination.equalsIgnoreCase("cassandra")) {
                return CASSANDRA;
            }
            else if (destination.equalsIgnoreCase("kafka")) {
                return KAFKA;
            }
            else if (destination.equalsIgnoreCase("hbase")) {
                return HBASE;
            }
            else if (destination.equalsIgnoreCase("hdfs")) {
                return HDFS;
            }
            else if (destination.equalsIgnoreCase("sql")) {
                return SQL;
            }
            else {
                return NONE;
            }
        }
    };

    enum RedisDataType {
        HASH,
        LIST,
        SET,
        STRING,
        ZSET,
        NONE;
    };

    enum EtlProcessType {
        NORMAL,
        INDEX_TO_VALUE //index=mfg_key, value=mfg_value
        ;
    };

}

