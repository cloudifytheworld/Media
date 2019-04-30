package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgSystemTable;
import com.huawei.mfg.bean.MfgSystemTableTopic;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import com.huawei.mfg.util.MfgException;
import com.huawei.mfg.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MfgSystemTableToTopicDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTableToTopicDAO.class);

    public MfgSystemTableToTopicDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgSystemTableTopic> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTableTopic> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_system_table_to_topic")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableTopic entity = BeanBuilders.buildMfgSystemTableTopic(rs);
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

    private static final String FETCH_TABLE_TOPIC_QUERY =
            "SELECT t.id AS tid, t.table_name, tt.id AS topic_id, tt.topic" +
            "  FROM mfg_system_table t, mfg_system_table_to_topic tt " +
            " WHERE t.id = tt.table_id";
    public List<Pair<MfgSystemTable, MfgSystemTableTopic>> fetchAllTableTopicMapping() throws SQLException {
        Connection conn = null;
        List<Pair<MfgSystemTable, MfgSystemTableTopic>> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement()
            ) {
                ResultSet rs = stmt.executeQuery(FETCH_TABLE_TOPIC_QUERY);
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

    private static final String FETCH_TABLE_TOPIC_QUERY_BY_TABLE_NAME = "SELECT t.id AS tid, t.table_name, tt.id AS topic_id, tt.topic" +
            "  FROM mfg_system_table t, mfg_system_table_to_topic tt" +
            " WHERE t.id = tt.table_id " +
            "   AND t.enabled='yes' AND tt.enabled='yes'" +
            "   AND t.table_name = ?";
    public List<Pair<MfgSystemTable, MfgSystemTableTopic>> fetchAllTableTopicMappingByTableName(String tableName) throws SQLException {
        Connection conn = null;
        List<Pair<MfgSystemTable, MfgSystemTableTopic>> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_TOPIC_QUERY_BY_TABLE_NAME)) {
                pstmt.setString(1, tableName);
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


    private static final String FETCH_TABLE_TOPIC_QUERY_BY_SYSTEM_NAME = "SELECT t.id AS tid, t.table_name, tt.id AS topic_id, tt.topic" +
            "  FROM mfg_system_table t, mfg_system_table_to_topic tt, mfg_system s" +
            " WHERE t.id = tt.table_id and t.system_id = s.id  " +
            "   AND t.enabled='yes' AND tt.enabled='yes'" +
            "   AND s.name = ?";
    public List<Pair<MfgSystemTable, MfgSystemTableTopic>> fetchAllTableTopicMappingBySystemName(String systemName) throws SQLException {
        Connection conn = null;
        List<Pair<MfgSystemTable, MfgSystemTableTopic>> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_TOPIC_QUERY_BY_SYSTEM_NAME)) {
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

    public Integer getTopicId(int tableId) throws SQLException {
        return this.getIdByName("mfg_system_table_to_topic", "table_id", tableId);
    }

    private static final String INSERT_Q = "INSERT INTO mfg_system_table_to_topic (table_id, topic, enabled, created_by) VALUES (?, ?, ?, ?)";
    public int persist(int tableId, String topic, boolean enabled, String username) throws MfgException {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, tableId);
                pstmt.setString(2, topic);
                pstmt.setString(3, (enabled?"yes":"no"));
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
            throw new MfgException(String.format("Exception caught when saving mfg system table to topic '%s'", topic), e);
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

            MfgSystemTableToTopicDAO dao = new MfgSystemTableToTopicDAO(ds);
            List<MfgSystemTableTopic> entities = dao.fetchAll();
            logger.debug("{}", entities);

            List<Pair<MfgSystemTable, MfgSystemTableTopic>> topEntities = dao.fetchAllTableTopicMappingBySystemName("aoi_single_component_image");
            logger.debug("{}", topEntities);
            logger.debug("{}", dao.fetchAllTableTopicMapping());
            logger.debug("{}", dao.fetchAllTableTopicMappingByTableName("device_status"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
