package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgSystemTablePkOffset;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import com.huawei.mfg.util.MfgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MfgSystemTablePkOffsetDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTablePkOffsetDAO.class);

    public MfgSystemTablePkOffsetDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgSystemTablePkOffset> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTablePkOffset> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_system_table_pk_offset")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTablePkOffset entity = BeanBuilders.buildMfgSystemTablePkOffset(rs);
                    if (entity != null) entities.add(entity);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null && !conn.isClosed()) conn.close();
        }
        return entities;
    }

    private static final String FETCH_ALL_BY_TABLE_NAME_Q =
                    "SELECT a.* " +
                    "  FROM mfg_system_table_pk_offset a, mfg_system_table b " +
                    " WHERE a.table_id = b.id AND table_name=?";
    public MfgSystemTablePkOffset fetchByTableName(String tableName) throws SQLException {
        Connection conn = null;
        MfgSystemTablePkOffset entity = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_BY_TABLE_NAME_Q);
            ) {
                pstmt.setString(1, tableName);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    entity = BeanBuilders.buildMfgSystemTablePkOffset(rs);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return entity;
    }

    private static final String INSERT_Q = "INSERT INTO mfg_system_table_pk_offset (table_id, last_offset, created_by) VALUES (?, ?, ?)";
    public int persist(String tableName, int tableId, String lastOffset, String username) throws MfgException
    {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, tableId);
                pstmt.setString(2, lastOffset);
                pstmt.setString(3, username);
                pstmt.executeUpdate();
                rs = pstmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg system table table pk offset '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    private static final String UPDATE_Q = "UPDATE mfg_system_table_pk_offset SET last_offset = ?, created_by = ?, last_updated=CURRENT_TIMESTAMP WHERE id = ?";
    public void update(String tableName, int pkOffsetTableId, String lastOffset, String username) throws MfgException {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                pstmt.setString(1, lastOffset);
                pstmt.setString(2, username);
                pstmt.setInt(3, pkOffsetTableId);
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg system table table pk offset '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
    }

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        try {
            SQLDatasource ds = new SQLDatasource(config);
            ds.connect();

            MfgSystemTablePkOffsetDAO dao = new MfgSystemTablePkOffsetDAO(ds);
            List<MfgSystemTablePkOffset> entities = dao.fetchAll();
            logger.debug("{}", entities);
            logger.debug("{}", dao.fetchByTableName("device_status"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
