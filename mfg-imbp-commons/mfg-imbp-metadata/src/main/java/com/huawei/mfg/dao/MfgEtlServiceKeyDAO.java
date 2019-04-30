package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgEtlServiceKey;
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
public class MfgEtlServiceKeyDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgEtlServiceKeyDAO.class);

    public MfgEtlServiceKeyDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgEtlServiceKey> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgEtlServiceKey> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_etl_service_key ORDER BY service_key ASC")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgEtlServiceKey entity = BeanBuilders.buildMfgEtlServiceKey(rs);
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

    public List<MfgEtlServiceKey> fetchAllEnabled() throws SQLException {
        Connection conn = null;
        List<MfgEtlServiceKey> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_etl_service_key WHERE enabled = 'yes' ORDER BY service_key ASC")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgEtlServiceKey entity = BeanBuilders.buildMfgEtlServiceKey(rs);
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

    private static final String INSERT_Q = "INSERT INTO mfg_etl_service_key (service_key, source, enabled, description, created_by) VALUES (?, ?, ?, ?, ?)";
    public int persist(MfgEtlServiceKey serviceKey, String username) throws MfgException {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, serviceKey.getServiceKey());
                pstmt.setString(2, serviceKey.getSource());
                pstmt.setString(3, serviceKey.isEnabled() ? "yes" : "no");
                pstmt.setString(4, serviceKey.getDescription());
                pstmt.setString(5, username);
                pstmt.executeUpdate();
                rs = pstmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg_etl_service_key '%s'", serviceKey.getServiceKey()), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    private static final String UPDATE_Q = "UPDATE mfg_etl_service_key " +
            "SET service_key = ?, source = ?, enabled = ?, description = ?, last_updated = ?, updated_by = ?" +
            " WHERE id = ?";
    public void update(MfgEtlServiceKey serviceKey, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                pstmt.setString(1, serviceKey.getServiceKey());
                pstmt.setString(2, serviceKey.getSource());
                pstmt.setString(3, serviceKey.isEnabled()?"yes":"no");
                pstmt.setString(4, serviceKey.getDescription());
                pstmt.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(6, username);
                pstmt.setInt(7, serviceKey.getId());
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg_etl_service_key '%s'", serviceKey.getServiceKey()), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    private static final String DISABLE_KEY_UPDATE_Q = "UPDATE mfg_etl_service_key " +
            "SET enabled = ?, last_updated = ?, updated_by = ? WHERE id = ?";
    public void disable(int id, String username) throws MfgException {
        disEnable(id, "no", username);
    }

    public void enable(int id, String username) throws MfgException {
        disEnable(id, "yes", username);
    }

    private void disEnable(int id, String enabled, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(DISABLE_KEY_UPDATE_Q)) {
                pstmt.setString(1, enabled);
                pstmt.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(3, username);
                pstmt.setInt(4, id);
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when dis/enable mfg_etl_service_key '%s'", id), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteById(int id) throws MfgException {
        this.deleteById("mfg_etl_service_key", id);
    }

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        try {
            SQLDatasource ds = new SQLDatasource(config);
            ds.connect();

            MfgEtlServiceKeyDAO dao = new MfgEtlServiceKeyDAO(ds);
            List<MfgEtlServiceKey> entities = dao.fetchAll();
            logger.debug("{}", entities);

//            MfgEtlServiceKey cfg = new MfgEtlServiceKey();
//            cfg.setServiceKey("/config/imbp-gateway/url/etl-aoi");
//            dao.persist(cfg, "sdu");

            entities = dao.fetchAll();
            logger.debug("{}", entities);

//            dao.deleteById(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
