package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgSystemTableToCassandra;
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
public class MfgSystemTableToCassandraDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTableToCassandraDAO.class);

    public MfgSystemTableToCassandraDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgSystemTableToCassandra> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTableToCassandra> entities = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement()) {
                rs = stmt.executeQuery("SELECT * FROM mfg_system_table_to_cassandra");
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableToCassandra entity = BeanBuilders.buildMfgSystemTableToCassandra(rs);
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

    private static final String FETCH_ALL_BY_TABLE_NAME_Q =
                    "SELECT c.* " +
                    "  FROM mfg_system_table_to_cassandra c, mfg_system_table t" +
                    " WHERE c.table_id = t.id" +
//                    "   AND t.enabled='yes' AND c.enabled='yes'" +
                    "   AND t.table_name = ?";
    public List<MfgSystemTableToCassandra> fetchAllByTableName(String tableName) throws SQLException {
        Connection conn = null;
        List<MfgSystemTableToCassandra> entities = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_BY_TABLE_NAME_Q);
            ) {
                pstmt.setString(1, tableName);
                rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableToCassandra entity = BeanBuilders.buildMfgSystemTableToCassandra(rs);
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

    private static final String FETCH_BY_SYSTEM_TABLE_ID_Q =
            "SELECT c.* FROM mfg_system_table_to_cassandra c " +
                    " WHERE c.table_id = ?";
//                    "   AND t.enabled='yes' AND c.enabled='yes'" +
    public MfgSystemTableToCassandra fetchByTableId(int id) throws SQLException {
        Connection conn = null;
        MfgSystemTableToCassandra entity = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_BY_SYSTEM_TABLE_ID_Q);) {
                pstmt.setInt(1, id);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                   entity = BeanBuilders.buildMfgSystemTableToCassandra(rs);
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

    private static final String FETCH_ALL_BY_SYSTEM_Q =
                    "SELECT c.* " +
                    "  FROM mfg_system_table_to_cassandra c, mfg_system_table t, mfg_system s" +
                    " WHERE t.system_id = s.id AND c.table_id = t.id" +
//                    "   AND t.enabled='yes' AND c.enabled='yes'" +
                    "   AND s.name = ?";
    public List<MfgSystemTableToCassandra> fetchAllBySystemName(String systemName) throws SQLException {
        Connection conn = null;
        List<MfgSystemTableToCassandra> entities = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_BY_SYSTEM_Q);
            ) {
                pstmt.setString(1, systemName);
                rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableToCassandra entity = BeanBuilders.buildMfgSystemTableToCassandra(rs);
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

    public Integer getCassandraTableId(int tableId, String tableName) throws SQLException {
        Connection conn = null;
        Integer id = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();

            try (PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM mfg_system_table_to_cassandra WHERE table_id=? AND table_name = ?");) {
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

    private static final String INSERT_Q =
            "INSERT INTO mfg_system_table_to_cassandra " +
            "(table_id, table_name, key_space, partition_keys, clustering_keys, enabled, created_by, description)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public int persist(String tableName, MfgSystemTableToCassandra systemTableToCassandra, String username) throws MfgException {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, systemTableToCassandra.getTableId());
                pstmt.setString(2, systemTableToCassandra.getTableName());
                pstmt.setString(3, systemTableToCassandra.getKeySpace());
                pstmt.setString(4, systemTableToCassandra.getPartitionKeys());
                pstmt.setString(5, systemTableToCassandra.getClusteringKeys());
                pstmt.setString(6, (systemTableToCassandra.isEnabled()?"yes":"no"));
                pstmt.setString(7, username);
                pstmt.setString(8, systemTableToCassandra.getDescription());
                pstmt.executeUpdate();
                rs = pstmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg system table to cassandra '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    private static final String UPDATE_Q =
            "UPDATE mfg_system_table_to_cassandra " +
                    " SET table_name = ?, key_space = ?, partition_keys = ?, clustering_keys = ?, enabled = ?, description = ?," +
                    " last_updated = ?, updated_by = ?" +
                    " WHERE id = ?";
    public void update(String tableName, MfgSystemTableToCassandra systemTableToCassandra, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                pstmt.setString(1, systemTableToCassandra.getTableName());
                pstmt.setString(2, systemTableToCassandra.getKeySpace());
                pstmt.setString(3, systemTableToCassandra.getPartitionKeys());
                pstmt.setString(4, systemTableToCassandra.getClusteringKeys());
                pstmt.setString(5, (systemTableToCassandra.isEnabled()?"yes":"no"));
                pstmt.setString(6, systemTableToCassandra.getDescription());
                pstmt.setTimestamp(7, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(8, username);
                pstmt.setInt(9, systemTableToCassandra.getId());
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg system table to cassandra '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteById(int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            String q = "DELETE FROM mfg_system_table_to_cassandra WHERE id = " + id;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting mfg_system_table_to_cassandra with id '%s'", id), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        try {
            SQLDatasource ds = new SQLDatasource(config);
            ds.connect();

            MfgSystemTableToCassandraDAO dao = new MfgSystemTableToCassandraDAO(ds);
            List<MfgSystemTableToCassandra> entities = dao.fetchAll();
            logger.debug("{}", entities);
            logger.debug("{}", dao.fetchAllByTableName("device_status"));
            logger.debug("{}", dao.fetchAllBySystemName("agv"));
//            logger.debug("{}", dao.fetchAllByTableName("device_status"));
//            logger.debug("{}", dao.fetchAllBySystemName("agv"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
