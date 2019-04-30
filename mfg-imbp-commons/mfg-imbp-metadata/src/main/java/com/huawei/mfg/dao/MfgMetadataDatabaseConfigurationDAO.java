package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgMetadataDatabaseConfiguration;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import com.huawei.mfg.util.MfgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MfgMetadataDatabaseConfigurationDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgMetadataDatabaseConfigurationDAO.class);

    public MfgMetadataDatabaseConfigurationDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgMetadataDatabaseConfiguration> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgMetadataDatabaseConfiguration> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_metadata_database_config")) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgMetadataDatabaseConfiguration entity = BeanBuilders.buildMfgMetadataDatabaseConfiguration(rs);
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

    public MfgMetadataDatabaseConfiguration fetchById(int configId) throws SQLException {
        Connection conn = null;
        MfgMetadataDatabaseConfiguration entity = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_metadata_database_config WHERE id = " + configId)) {
                while (rs.next()) {
                    entity = BeanBuilders.buildMfgMetadataDatabaseConfiguration(rs);
                    break;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null && !conn.isClosed()) conn.close();
        }
        return entity;
    }

    public List<MfgMetadataDatabaseConfiguration> fetchLatestN(int limit) throws SQLException {
        Connection conn = null;
        List<MfgMetadataDatabaseConfiguration> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_metadata_database_config ORDER BY created DESC LIMIT " + limit)) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgMetadataDatabaseConfiguration entity = BeanBuilders.buildMfgMetadataDatabaseConfiguration(rs);
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

    private static final String INSERT_Q = "INSERT INTO mfg_metadata_database_config" +
            " (host, port, db_type, db_name, username, password, connection_pool_name, test_query, description, created_by)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public int persist(MfgMetadataDatabaseConfiguration config, String username) throws MfgException {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, config.getHost());
                pstmt.setInt(2, config.getPort());
                pstmt.setString(3, config.getDatabaseType());
                pstmt.setString(4, config.getDatabase());
                pstmt.setString(5, config.getUsername());
                pstmt.setString(6, config.getPassword());
                pstmt.setString(7, config.getConnectionPoolName());
                pstmt.setString(8, config.getTestQuery());
                pstmt.setString(9, config.getDescription());
                pstmt.setString(10, username);
                pstmt.executeUpdate();
                rs = pstmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException("Exception caught when saving mfg_metadata_database_config", e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }


    private static final String UPDATE_Q = "UPDATE mfg_metadata_database_config" +
            " SET host = ?, port = ?, db_type = ?, db_name = ?, username = ?, " +
            " password = ?, connection_pool_name = ?, test_query = ?, description = ?, last_updated = ?, " +
            " updated_by = ?" +
            " WHERE id = ?";
    public void update(MfgMetadataDatabaseConfiguration config, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                pstmt.setString(1, config.getHost());
                pstmt.setInt(2, config.getPort());
                pstmt.setString(3, config.getDatabaseType());
                pstmt.setString(4, config.getDatabase());
                pstmt.setString(5, config.getUsername());
                pstmt.setString(6, config.getPassword());
                pstmt.setString(7, config.getConnectionPoolName());
                pstmt.setString(8, config.getTestQuery());
                pstmt.setString(9, config.getDescription());
                pstmt.setTimestamp(10, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(11, username);
                pstmt.setInt(12, config.getId());
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException("Exception caught when saving mfg_metadata_database_config", e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteById(int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            String q = "DELETE FROM mfg_metadata_database_config WHERE id = " + id;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting mfg_metadata_database_config with id '%s'", id), e);
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

            MfgMetadataDatabaseConfigurationDAO dao = new MfgMetadataDatabaseConfigurationDAO(ds);
            List<MfgMetadataDatabaseConfiguration> entities = dao.fetchAll();
            logger.debug("{}", entities);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
