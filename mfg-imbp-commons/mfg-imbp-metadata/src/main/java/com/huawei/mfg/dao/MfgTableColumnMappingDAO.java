package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgTableColumnMapping;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import com.huawei.mfg.util.MfgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MfgTableColumnMappingDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgTableColumnMappingDAO.class);
    private static final String TABLE_NAME = "mfg_table_column_mapping";

    public MfgTableColumnMappingDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgTableColumnMapping> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgTableColumnMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM %s", TABLE_NAME));
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgTableColumnMapping entity = BeanBuilders.buildMfgTableColumnMapping(rs);
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

    private static final String INSERT_Q = String.format("INSERT INTO %s (column_name, abbr_column_name, concise_abbr_column_name, description, created_by) VALUES (?, ?, ?, ?, ?)", TABLE_NAME);
    public int persist(String columnName, String abbrColumnName, String conciseAbbrColumnName, String description, String username) throws MfgException {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, columnName);
                pstmt.setString(2, abbrColumnName);
                pstmt.setString(3, conciseAbbrColumnName);
                pstmt.setString(4, description);
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
            throw new MfgException(String.format("Exception caught when saving '%s' table", TABLE_NAME), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        SQLDatasource ds = null;
        try {
            ds = new SQLDatasource(config);
            ds.connect();

            MfgTableColumnMappingDAO dao = new MfgTableColumnMappingDAO(ds);
            List<MfgTableColumnMapping> entities = dao.fetchAll();
            logger.debug("{}", entities);
            logger.debug("# of mfg_table_column_mapping: {}", entities.size());

//            String columnName = "all_cmd_complete_flag";
//            String abbrColumnName = "all_cmd_cmp_flg";
//            String conciseAbbrColumnName = "allcmdcmpflg";
//            String description = "All Command Complete Flag";
//            String username = "sdu";
//            dao.persist(columnName, abbrColumnName, conciseAbbrColumnName, description, username);

//            logger.debug("{}", dao.fetchAllTableTopicMapping());
//            logger.debug("{}", dao.fetchAllTableTopicMappingByTableName("device_status"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (ds != null) {
                try { ds.close(); } catch (SQLException e) {}
            }
        }
    }

}
