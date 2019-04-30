package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgSystemTableMapping;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MfgSystemTableMappingDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTableMappingDAO.class);

    public MfgSystemTableMappingDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgSystemTableMapping> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTableMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_system_table_mapping")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableMapping entity = BeanBuilders.buildMfgSystemTableMapping(rs);
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

    private static final String FETCH_ALL_BY_TABLENAME_Q =
            "SELECT m.* " +
            "  FROM mfg_system_table_mapping m, mfg_system_table t" +
            " WHERE m.table_id = t.id AND t.table_name = ?";
    public MfgSystemTableMapping fetchAllByTableName(String name) throws SQLException {
        Connection conn = null;
        MfgSystemTableMapping entity = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_BY_TABLENAME_Q);
            ) {
                pstmt.setString(1, name);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    entity = BeanBuilders.buildMfgSystemTableMapping(rs);
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

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        try {
            SQLDatasource ds = new SQLDatasource(config);
            ds.connect();

            MfgSystemTableMappingDAO dao = new MfgSystemTableMappingDAO(ds);
            List<MfgSystemTableMapping> entities = dao.fetchAll();
            logger.debug("{}", entities);
//            logger.debug("{}", dao.fetchById(1));
//            logger.debug("{}", dao.fetchById(2));
//
            logger.debug("{}", dao.fetchAllByTableName("device_status"));
            logger.debug("{}", dao.fetchAllByTableName("person"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
