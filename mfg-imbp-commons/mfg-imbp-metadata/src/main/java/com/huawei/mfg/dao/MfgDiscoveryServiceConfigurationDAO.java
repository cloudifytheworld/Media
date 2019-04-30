package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgDiscoveryServiceConfiguration;
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
public class MfgDiscoveryServiceConfigurationDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgDiscoveryServiceConfigurationDAO.class);

    public MfgDiscoveryServiceConfigurationDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgDiscoveryServiceConfiguration> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgDiscoveryServiceConfiguration> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_discovery_service_config ORDER BY created DESC")) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgDiscoveryServiceConfiguration entity = BeanBuilders.buildMfgDiscoveryServiceConfiguration(rs);
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

    public List<MfgDiscoveryServiceConfiguration> fetchLatestN(int limit) throws SQLException {
        Connection conn = null;
        List<MfgDiscoveryServiceConfiguration> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_discovery_service_config ORDER BY created DESC LIMIT " + limit)) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgDiscoveryServiceConfiguration entity = BeanBuilders.buildMfgDiscoveryServiceConfiguration(rs);
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

    private static final String INSERT_Q = "INSERT INTO mfg_discovery_service_config (host, port, description, created_by) VALUES (?, ?, ?, ?)";
    public int persist(MfgDiscoveryServiceConfiguration config, String username) throws MfgException {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, config.getHost());
                pstmt.setInt(2, config.getPort());
                pstmt.setString(3, config.getDescription());
                pstmt.setString(4, username);
                pstmt.executeUpdate();
                rs = pstmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException("Exception caught when saving mfg_discovery_service_config", e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    private static final String UPDATE_Q = "UPDATE mfg_discovery_service_config SET host = ?, port = ?, description = ?, last_updated = ?, updated_by = ? WHERE id = ?";
    public void update(MfgDiscoveryServiceConfiguration config, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                pstmt.setString(1, config.getHost());
                pstmt.setInt(2, config.getPort());
                pstmt.setString(3, config.getDescription());
                pstmt.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(5, username);
                pstmt.setInt(6, config.getId());
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException("Exception caught when saving mfg_discovery_service_config", e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteById(int id) throws MfgException {
        this.deleteById("mfg_discovery_service_config", id);
    }

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        try {
            SQLDatasource ds = new SQLDatasource(config);
            ds.connect();

            MfgDiscoveryServiceConfigurationDAO dao = new MfgDiscoveryServiceConfigurationDAO(ds);
            List<MfgDiscoveryServiceConfiguration> entities = dao.fetchAll();
            logger.debug("{}", entities);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
