package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgSystem;
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
public class MfgSystemDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(BaseDAO.class);

    public MfgSystemDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgSystem> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystem> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_system")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystem entity = BeanBuilders.buildMfgSystem(rs);
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

    public MfgSystem fetchById(int id) throws SQLException {
        Connection conn = null;
        MfgSystem entity = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();

            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM mfg_system WHERE id=?");
            ) {
                pstmt.setInt(1, id);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    entity = BeanBuilders.buildMfgSystem(rs);
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

    public MfgSystem fetchByName(String name) throws SQLException {
        Connection conn = null;
        MfgSystem entity = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();

            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM mfg_system WHERE name=?");
            ) {
                pstmt.setString(1, name);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    entity = BeanBuilders.buildMfgSystem(rs);
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

    public Integer getSystemIdByName(String name) throws SQLException {
        return this.getIdByName("mfg_system", "name", name);
    }

    private static final String INSERT_Q = "INSERT INTO mfg_system (name, created_by) VALUES (?, ?)";
    public int persist(String systemName, String username) throws MfgException {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, systemName);
                pstmt.setString(2, username);
                pstmt.executeUpdate();
                rs = pstmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg system '%s'", systemName), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    public void deleteById(int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            String q = "DELETE FROM mfg_system WHERE id = " + id;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting mfg_system with id '%s'", id), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    private final static String UPDATE_NAME_BY_ID_Q = "UPDATE mfg_system SET name = ? WHERE id = ?";
    public void update(int id, String name) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_NAME_BY_ID_Q)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting mfg system id '%s'", id), e);
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

            MfgSystemDAO dao = new MfgSystemDAO(ds);
            List<MfgSystem> entities = dao.fetchAll();
            logger.debug("{}", entities);
//            logger.debug("{}", dao.fetchById(1));
//            logger.debug("{}", dao.fetchById(2));
//
//            logger.debug("{}", dao.fetchByName("test"));
//            logger.debug("{}", dao.fetchByName("agv"));
//            logger.debug("{}", dao.getSystemIdByName("agv"));
//            logger.debug("Saved Id: {}", dao.persist("abc", "sdu"));

//            dao.deleteById(3);

            dao.update(21, "cc");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (MfgException e) {
            e.printStackTrace();
        }
    }

}
