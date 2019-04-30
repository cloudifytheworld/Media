package com.huawei.mfg.dao;

import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.MfgException;

import java.sql.*;

public class BaseDAO {
    protected SQLDatasource sqlDatasource;

    public BaseDAO(SQLDatasource sqlDatasource) {
        this.sqlDatasource = sqlDatasource;
    }

    public Connection getConnection() throws SQLException {
        return this.sqlDatasource.getConnection();
    }

    public void closeAllQuietly(ResultSet rs, Connection conn, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
        }
    }

    public void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e1) { }
        }
    }

    public void setAutocommit(Connection conn, boolean autocommit) {
        if (conn != null) {
            try {
                conn.setAutoCommit(autocommit);
            } catch (SQLException e) { }
        }
    }

    public Integer getIdByName(String tableName, String field, String value) throws SQLException {
        Connection conn = null;
        Integer id = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();

            try (PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT id FROM %s WHERE %s=?", tableName, field));
            ) {
                pstmt.setString(1, value);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    public Integer getIdByName(String tableName, String field, int value) throws SQLException {
        Connection conn = null;
        Integer id = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();

            try (PreparedStatement pstmt = conn.prepareStatement(String.format("SELECT id FROM %s WHERE %s=?", tableName, field));
            ) {
                pstmt.setInt(1, value);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    public void deleteById(String dbTable, int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            String q = String.format("DELETE FROM %s WHERE id = %d", dbTable, id);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting %s with id '%d'", dbTable, id), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    static MfgJDBCDatasourceConfiguration createTestConfig() {
        MfgJDBCDatasourceConfiguration config = new MfgJDBCDatasourceConfiguration();
        config.setPoolName("mfg-jdbc-connection-pool");
//        hikariConfig.setJdbcUrl("jdbc:mysql://10.208.51.172:3306/mfg_metadata");
        config.setUsername("mfg");
        config.setPassword("password");
        config.setDatasourceClassname("org.mariadb.jdbc.MariaDbDataSource");
        config.setConnectionTestQuery("SELECT 1");
        config.setHost("10.208.51.172");
        config.setPort(3306);
        config.setDatabaseName("mfg_metadata");
        config.setDatabaseName("mfg_metadata_test");
        config.setProperties("useUnicode=true;characterEncoding=utf8&autoReconnect=true");

        return config;
    }
}
