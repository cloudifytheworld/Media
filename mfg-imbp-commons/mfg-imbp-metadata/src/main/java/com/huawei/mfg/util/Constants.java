package com.huawei.mfg.util;

import com.google.common.base.Strings;

public interface Constants {
    public static final String MARIA_JDBC_DATASOURCE_CLASSNAME = "org.mariadb.jdbc.MariaDbDataSource";
    public static final String DEFAULT_MYSQL_CONNECTION_TEST_QUERY = "SELECT 1";
    //config.setProperties("useUnicode=true;characterEncoding=utf8&autoReconnect=true");
    public static final String MYSQL_CONNECTION_OPTIONS = "useUnicode=true;characterEncoding=utf8&autoReconnect=true";
    public static final int DEFAUL_MYSQL_SERVER_PORT = 3306;

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
    }
}

