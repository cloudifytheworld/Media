package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgSystemTable;
import com.huawei.mfg.bean.MfgSystemTableToHive;
import com.huawei.mfg.bean.MfgSystemTableTopic;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import com.huawei.mfg.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MfgSystemTableToHiveDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTableToHiveDAO.class);

    public MfgSystemTableToHiveDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgSystemTableToHive> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTableToHive> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_system_table_to_hive")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableToHive entity = BeanBuilders.buildMfgSystemTableToHive(rs);
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

    private static final String FETCH_TABLE_HIVE_QUERY_BY_SYSTEM_NAME = "SELECT t.id AS tid, t.table_name, tt.id AS hive_id, tt.hive" +
            "  FROM mfg_system_table t, mfg_system_table_to_hive tt, mfg_system s" +
            " WHERE t.id = tt.table_id and t.system_id = s.id  " +
            "   AND t.enabled='yes' AND tt.enabled='yes'" +
            "   AND s.name = ?";
    public List<Pair<MfgSystemTable, MfgSystemTableTopic>> fetchAllTableHiveMappingBySystemName(String systemName) throws SQLException {
        Connection conn = null;
        List<Pair<MfgSystemTable, MfgSystemTableTopic>> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_HIVE_QUERY_BY_SYSTEM_NAME)) {
                pstmt.setString(1, systemName);
                ResultSet rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    Pair<MfgSystemTable, MfgSystemTableTopic> pair = BeanBuilders.buildTableTopicPair(rs);
                    entities.add(pair);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        try {
            SQLDatasource ds = new SQLDatasource(config);
            ds.connect();

            MfgSystemTableToHiveDAO dao = new MfgSystemTableToHiveDAO(ds);
            List<MfgSystemTableToHive> entities = dao.fetchAll();
            logger.debug("{}", entities);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
