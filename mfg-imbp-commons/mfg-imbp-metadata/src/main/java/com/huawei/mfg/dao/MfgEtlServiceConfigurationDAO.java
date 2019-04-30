package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgEtlServiceConfiguration;
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
public class MfgEtlServiceConfigurationDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgEtlServiceConfigurationDAO.class);

    public MfgEtlServiceConfigurationDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgEtlServiceConfiguration> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgEtlServiceConfiguration> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_etl_service_config")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgEtlServiceConfiguration entity = BeanBuilders.buildMfgEtlServiceConfiguration(rs);
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

    private final static String LATEST_N_BY_SERVICE_KEY_Q = "SELECT * FROM mfg_etl_service_config WHERE key_id = '%d' ORDER BY created DESC LIMIT %d";
    public List<MfgEtlServiceConfiguration> fetchLatestNByServiceKey(int keyId, int limit) throws SQLException {
        Connection conn = null;
        List<MfgEtlServiceConfiguration> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(String.format(LATEST_N_BY_SERVICE_KEY_Q, keyId, limit))
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgEtlServiceConfiguration entity = BeanBuilders.buildMfgEtlServiceConfiguration(rs);
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

    private static final String INSERT_Q = "INSERT INTO mfg_etl_service_config (key_id, service_value, description, created_by) VALUES (?, ?, ?, ?)";
    public int persist(MfgEtlServiceConfiguration config, String username) throws MfgException {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, config.getKeyId());
                pstmt.setString(2, config.getServiceValue());
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
            throw new MfgException(String.format("Exception caught when saving mfg_etl_service_config '%s'", config.getKeyId()), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    private static final String UPDATE_Q = "UPDATE mfg_etl_service_config " +
            "SET key_id = ?, service_value = ?, description = ?," +
            " last_updated = ?, updated_by = ?" +
            " WHERE id = ?";
    public void update(MfgEtlServiceConfiguration config, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                pstmt.setInt(1, config.getKeyId());
                pstmt.setString(2, config.getServiceValue());
                pstmt.setString(3, config.getDescription());
                pstmt.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(5, username);
                pstmt.setInt(6, config.getId());
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg_etl_service_config '%s'", config.getKeyId()), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteById(int id) throws MfgException {
        this.deleteById("mfg_etl_service_config", id);
    }

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        try {
            SQLDatasource ds = new SQLDatasource(config);
            ds.connect();

            MfgEtlServiceConfigurationDAO dao = new MfgEtlServiceConfigurationDAO(ds);
            List<MfgEtlServiceConfiguration> entities = dao.fetchAll();
            logger.debug("{}", entities);

            MfgEtlServiceConfiguration cfg = new MfgEtlServiceConfiguration();
            cfg.setKeyId(3);
            cfg.setServiceValue("http://10.208.51.174");
            dao.persist(cfg, "sdu");

            entities = dao.fetchAll();
            logger.debug("{}", entities);

//            dao.deleteById(1);

            entities = dao.fetchLatestNByServiceKey(1, 10);
            logger.debug("{}", entities);
            logger.debug("{}", entities.size());
        } catch (SQLException | MfgException e) {
            e.printStackTrace();
        }
    }
}
