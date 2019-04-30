package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgSystemTableColumnMapping;
import com.huawei.mfg.bean.TableColumnsAndMapping;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MfgSystemTableColumnMappingDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTableColumnMappingDAO.class);

    public MfgSystemTableColumnMappingDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }


    public List<MfgSystemTableColumnMapping> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTableColumnMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_system_table_column_mapping")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableColumnMapping entity = BeanBuilders.buildMfgSystemTableColumnMapping(rs);
                    if (entity != null) entities.add(entity);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    private static final String FETCH_TABLE_COLUMNS_MAPPED_COLUMNS =
            "SELECT t.table_name, c.col_name, c.col_data_type, m.short_col_name, m.for_db, m.col_data_type AS mapped_data_type, m.column_family" +
                    "  FROM mfg_system_table t, mfg_system_table_column c, mfg_system_table_column_mapping m" +
                    " WHERE t.id = c.table_id AND c.id = m.col_id";
    public List<TableColumnsAndMapping> fetchAllTableColumnsAndMapping() throws SQLException {
        Connection conn = null;
        List<TableColumnsAndMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement()
            ) {
                ResultSet rs = stmt.executeQuery(FETCH_TABLE_COLUMNS_MAPPED_COLUMNS);
                entities = new ArrayList<>();
                while (rs.next()) {
                    TableColumnsAndMapping mapping = BeanBuilders.buildTableColumnsAndMapping(rs);
                    entities.add(mapping);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    private static final String FETCH_TABLE_COLUMNS_MAPPED_COLUMNS_BY_TABLE_NAME =
            "SELECT t.table_name, c.col_name, c.col_data_type, m.short_col_name, m.for_db, m.col_data_type AS mapped_data_type, m.column_family" +
            "  FROM mfg_system_table t, mfg_system_table_column c, mfg_system_table_column_mapping m" +
            " WHERE t.id = c.table_id AND c.id = m.col_id AND t.table_name = ?";
    public List<TableColumnsAndMapping> fetchAllTableColumnsAndMappingByTableName(String tableName) throws SQLException {
        Connection conn = null;
        List<TableColumnsAndMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_COLUMNS_MAPPED_COLUMNS_BY_TABLE_NAME);
            ) {
                pstmt.setString(1, tableName);
                ResultSet rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    TableColumnsAndMapping mapping = BeanBuilders.buildTableColumnsAndMapping(rs);
                    entities.add(mapping);
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

            MfgSystemTableColumnMappingDAO dao = new MfgSystemTableColumnMappingDAO(ds);
            List<MfgSystemTableColumnMapping> entities = dao.fetchAll();
            logger.debug("{}", entities);
//            logger.debug("{}", dao.fetchById(1));
//            logger.debug("{}", dao.fetchById(2));
//
//            logger.debug("{}", dao.fetchAllByTableName("person"));
//            logger.debug("{}", dao.fetchAllByTableName("person"));
            logger.debug("{}", dao.fetchAllTableColumnsAndMappingByTableName("person"));

            logger.debug("{}", dao.fetchAllTableColumnsAndMapping());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
