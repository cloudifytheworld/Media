package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgSystemTableToHbase;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import com.huawei.mfg.util.MfgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class MfgSystemTableToHbaseDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTableToHbaseDAO.class);

    public MfgSystemTableToHbaseDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgSystemTableToHbase> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTableToHbase> entities = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement()) {
                rs = stmt.executeQuery("SELECT * FROM mfg_system_table_to_hbase");
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableToHbase entity = BeanBuilders.buildMfgSystemTableToHbase(rs);
                    if (entity != null) entities.add(entity);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return entities;
    }

    private static final String FETCH_ALL_BY_TABLENAME_Q =
            "SELECT h.* " +
                    "  FROM mfg_system_table_to_hbase h, mfg_system_table t" +
                    " WHERE h.table_id = t.id" +
//                    "   AND t.enabled='yes' AND h.enabled='yes'" +
                    "   AND t.table_name = ?";
    public List<MfgSystemTableToHbase> fetchAllByTableName(String tableName) throws SQLException {
        Connection conn = null;
        List<MfgSystemTableToHbase> entities = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_BY_TABLENAME_Q);
            ) {
                pstmt.setString(1, tableName);
                rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableToHbase entity = BeanBuilders.buildMfgSystemTableToHbase(rs);
                    if (entity != null) entities.add(entity);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return entities;
    }

    private static final String FETCH_ALL_BY_SYSTEM_Q =
            "SELECT h.* " +
                    "  FROM mfg_system_table_to_hbase h, mfg_system_table t, mfg_system s" +
                    " WHERE t.system_id = s.id AND h.table_id = t.id" +
//                    "   AND t.enabled='yes' AND h.enabled='yes'" +
                    "   AND s.name = ?";
    public List<MfgSystemTableToHbase> fetchAllBySystemName(String systemName) throws SQLException {
        Connection conn = null;
        List<MfgSystemTableToHbase> entities = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_BY_SYSTEM_Q);
            ) {
                pstmt.setString(1, systemName);
                rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableToHbase entity = BeanBuilders.buildMfgSystemTableToHbase(rs);
                    if (entity != null) entities.add(entity);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return entities;
    }

    public Integer getHbaseTableId(int tableId, String tableName) throws SQLException {
        Connection conn = null;
        Integer id = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();

            try (PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM mfg_system_table_to_hbase WHERE table_id=? AND table_name = ?");
            ) {
                pstmt.setInt(1, tableId);
                pstmt.setString(2, tableName);
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

    private static final String INSERT_Q = "INSERT INTO mfg_system_table_to_hbase (table_id, table_name, column_families, row_key_columns, enabled, created_by) VALUES (?, ?, ?, ?, ?, ?)";
    public int persist(int tableId, String tableName, String columnFamilies, String rowKeyColumns, boolean enabled, String username) throws MfgException {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, tableId);
                pstmt.setString(2, tableName);
                pstmt.setString(3, columnFamilies);
                pstmt.setString(4, rowKeyColumns);
                pstmt.setString(5, (enabled?"yes":"no"));
                pstmt.setString(6, username);
                pstmt.executeUpdate();
                rs = pstmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg system table to hbase '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        try {
            SQLDatasource ds = new SQLDatasource(config);
            ds.connect();

            MfgSystemTableToHbaseDAO dao = new MfgSystemTableToHbaseDAO(ds);
            List<MfgSystemTableToHbase> entities = dao.fetchAll();
            logger.debug("{}", entities);
            logger.debug("{}", dao.fetchAllByTableName("device_status"));
            logger.debug("{}", dao.fetchAllBySystemName("agv"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
