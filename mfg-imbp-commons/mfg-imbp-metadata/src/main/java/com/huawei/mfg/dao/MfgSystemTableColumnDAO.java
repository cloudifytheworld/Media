package com.huawei.mfg.dao;

import com.huawei.mfg.bean.MfgSystemTable;
import com.huawei.mfg.bean.MfgSystemTableColumn;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pojo.ColumnDef;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import com.huawei.mfg.util.MfgException;
import com.huawei.mfg.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class MfgSystemTableColumnDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTableColumnDAO.class);

    public MfgSystemTableColumnDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }


    public List<MfgSystemTableColumn> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTableColumn> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT *, id as cid FROM mfg_system_table_column")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableColumn entity = BeanBuilders.buildMfgSystemTableColumn(rs);
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
            "SELECT c.* " +
            "  FROM mfg_system_table_column c, mfg_system_table t" +
            " WHERE c.table_id = t.id AND t.table_name = ?";
    public List<MfgSystemTableColumn> fetchAllByTableName(String tableName) throws SQLException {
        Connection conn = null;
        List<MfgSystemTableColumn> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_BY_TABLENAME_Q);
            ) {
                pstmt.setString(1, tableName);
                ResultSet rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableColumn entity = BeanBuilders.buildMfgSystemTableColumn(rs);
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

    public int getColumnIdByName(int tableId, String columnName) throws SQLException {
        Connection conn = null;
        int id = 0;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();

            try (PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM mfg_system_table_column WHERE table_id = ? AND col_name=?");
            ) {
                pstmt.setInt(1, tableId);
                pstmt.setString(2, columnName);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    public List<Pair<Integer, String>> getColumnIdsByNames(int tableId, List<String> columnNames) throws SQLException {
        StringBuilder builder = new StringBuilder("SELECT id, col_name FROM mfg_system_table_column WHERE table_id = ").append(tableId).append(" AND col_name IN (");

        for (int i = 0; i < columnNames.size(); i++) {
            String col = columnNames.get(i);
            builder.append("\"").append(col).append("\"");
            if (i != columnNames.size() - 1) {
                builder.append(",");
            }
        }
        builder.append(")");

        Connection conn = null;
        ResultSet rs = null;
        List<Pair<Integer, String>> columnIdPairs = new ArrayList<>();

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement()) {
                rs = stmt.executeQuery(builder.toString());
                while (rs.next()) {
                    int cid = rs.getInt(1);
                    String colName = rs.getString(2);
                    columnIdPairs.add(Pair.of(cid, colName));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return columnIdPairs;
    }

    private static final String INSERT_Q = "INSERT INTO mfg_system_table_column (table_id, col_name, col_data_type, column_size, nullable, col_format, col_pattern, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public List<Pair<Integer, String>> persist(int tableId, String tableName, List<MfgSystemTableColumn> columns, String username) throws SQLException, MfgException {
        Connection conn = null;
        ResultSet rs = null;
        List<Pair<Integer, String>> columnIdPairs = new ArrayList<>();

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                for (MfgSystemTableColumn def : columns) {
                    pstmt.setInt(1, tableId);
                    pstmt.setString(2, def.getColumnNameLC());
                    pstmt.setString(3, def.getTypeName());
                    pstmt.setInt(4, def.getColumnSize());
                    pstmt.setString(5, def.isNullable() ? "yes" : "no");
                    pstmt.setString(6, def.getColumnFormat());
                    pstmt.setString(7, def.getColumnPattern());
                    pstmt.setString(8, username);
                    pstmt.addBatch();
                }

                int[] updateCounts = pstmt.executeBatch();
                rs = pstmt.getGeneratedKeys();
                int pos = 0;

                while (rs.next()) {
                    while (!(updateCounts[pos] >= 0 || updateCounts[pos] == Statement.SUCCESS_NO_INFO) && pos < columns.size()) {
                        pos++;
                    }

                    if (updateCounts[pos] >= 0 || updateCounts[pos] == Statement.SUCCESS_NO_INFO) {
                        columns.get(pos).setId(rs.getInt(1));
                        pos++;
                    }
                }
            }

            for (MfgSystemTableColumn def : columns) {
                columnIdPairs.add(Pair.of(def.getId(), def.getColumnNameLC()));
            }
        }
        catch (SQLException e) {
            throw new MfgException(String.format("Exception caught when saving mfg system table columns for table '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return columnIdPairs;
    }

    private static final String UPDATE_Q = "UPDATE mfg_system_table_column " +
            "SET table_id = ?, col_name = ?, col_data_type = ?, " +
            " last_updated = ?, updated_by = ?, column_size = ?, nullable = ?, col_format = ?, col_pattern = ?" +
            " WHERE id = ?";
    public void update(String tableName, int tableId, MfgSystemTableColumn column, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                pstmt.setInt(1, tableId);
                pstmt.setString(2, column.getColumnName());
                pstmt.setString(3, column.getTypeName());
                pstmt.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(5, username);
                pstmt.setInt(6, column.getColumnSize());
                pstmt.setString(7, column.isNullable() ? "yes" : "no");

                pstmt.setString(8, column.getColumnFormat());
                pstmt.setString(9, column.getColumnPattern());
                pstmt.setInt(10, column.getId());
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg system table '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteById(int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            String q = "DELETE FROM mfg_system_table_column WHERE id = " + id;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting mfg_system_table_column with id '%s'", id), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteByTableId(int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            String q = "DELETE FROM mfg_system_table_column WHERE table_id = " + id;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting mfg_system_table_column with id '%s'", id), e);
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

            MfgSystemTableColumnDAO dao = new MfgSystemTableColumnDAO(ds);
            List<MfgSystemTableColumn> entities = dao.fetchAll();
            logger.debug("{}", entities);
//            logger.debug("{}", dao.fetchById(1));
//            logger.debug("{}", dao.fetchById(2));
//
            logger.debug("{}", dao.fetchAllByTableName("device_status"));
            logger.debug("{}", dao.getColumnIdByName(2,"dc_id"));
            logger.debug("{}", dao.getColumnIdByName(2,"device_id"));

            int tableId = 7;
            String tableName = "mfg_system_table_to_topic";
            String username = "sdu";

            List<ColumnDef> columnDefs = new ArrayList<>();
//            columnDefs.add(ColumnDef.create("col-1", "VARCHAR", "VARCHAR", 16, 16, false, false, null, null));
//            columnDefs.add(ColumnDef.create("col-2", "VARCHAR", "VARCHAR", 16, 16, false, false, null, null));
//            columnDefs.add(ColumnDef.create("col-3", "VARCHAR", "VARCHAR", 16, 16, false, false, null, null));
//            dao.persist(tableId, tableName, columnDefs, username);
            columnDefs.forEach(e -> {
                logger.debug("{}", e);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
