package com.huawei.mfg.dao;

import com.huawei.mfg.bean.meta.MetadataColumnBean;
import com.huawei.mfg.bean.MfgSystemTableToHbaseColumnMapping;
import com.huawei.mfg.bean.TableToHbaseColumnsMapping;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import com.huawei.mfg.util.MfgException;
import com.huawei.mfg.util.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class MfgSystemTableToHbaseColumnMappingDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTableToHbaseColumnMappingDAO.class);

    public MfgSystemTableToHbaseColumnMappingDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }


    public List<MfgSystemTableToHbaseColumnMapping> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTableToHbaseColumnMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_system_table_to_hbase_column_mapping")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableToHbaseColumnMapping entity = BeanBuilders.buildMfgSystemTableToHbaseColumnMapping(rs);
                    if (entity != null) entities.add(entity);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    private static final String FETCH_TABLE_COLUMNS_MAPPED_HBASE_COLUMNS =
            "SELECT t.table_name, c.col_name, c.col_data_type, m.mapped_col_name, m.column_family, h.table_name AS hbase_table_name" +
                    "  FROM mfg_system_table t, mfg_system_table_column c, mfg_system_table_to_hbase_column_mapping m, mfg_system_table_to_hbase h" +
                    " WHERE t.id = c.table_id AND c.id = m.col_id AND h.table_id = t.id";
    public List<TableToHbaseColumnsMapping> fetchAllTableColumnsHbaseMapping() throws SQLException {
        Connection conn = null;
        List<TableToHbaseColumnsMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement()
            ) {
                ResultSet rs = stmt.executeQuery(FETCH_TABLE_COLUMNS_MAPPED_HBASE_COLUMNS);
                entities = new ArrayList<>();
                while (rs.next()) {
                    TableToHbaseColumnsMapping mapping = BeanBuilders.buildTableColumnsHbaseMapping(rs);
                    entities.add(mapping);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    public Map<String, List<TableToHbaseColumnsMapping>> getTableColumnColumnFamilyMapping() throws SQLException {
        List<TableToHbaseColumnsMapping> entities = this.fetchAllTableColumnsHbaseMapping();
        if (entities != null && !entities.isEmpty()) {
            Map<String, List<TableToHbaseColumnsMapping>> map = new HashMap<>();
            for (TableToHbaseColumnsMapping entity : entities) {
                if (!map.containsKey(entity.getTableName())) {
                    map.put(entity.getTableName(), new ArrayList<>());
                }
                map.get(entity.getTableName()).add(entity);
            }
            return map;
        }
        return null;
    }

    private static final String FETCH_TABLE_COLUMNS_MAPPED_HBASE_COLUMNS_BY_TABLE_NAME =
            "SELECT t.table_name, c.col_name, c.col_data_type, m.mapped_col_name, m.column_family, h.table_name AS hbase_table_name" +
            "  FROM mfg_system_table t, mfg_system_table_column c, mfg_system_table_to_hbase_column_mapping m, mfg_system_table_to_hbase h" +
            " WHERE t.id = c.table_id AND c.id = m.col_id AND h.table_id = t.id AND t.table_name = ?";
    public List<TableToHbaseColumnsMapping> fetchAllTableColumnsHbaseMappingByTableName(String tableName) throws SQLException {
        Connection conn = null;
        List<TableToHbaseColumnsMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_COLUMNS_MAPPED_HBASE_COLUMNS_BY_TABLE_NAME);
            ) {
                pstmt.setString(1, tableName);
                ResultSet rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    TableToHbaseColumnsMapping mapping = BeanBuilders.buildTableColumnsHbaseMapping(rs);
                    entities.add(mapping);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    private static final String FETCH_TABLE_COLUMNS_MAPPED_HBASE_COLUMNS_BY_SYSTEM =
            "SELECT t.table_name, c.col_name, c.col_data_type, m.mapped_col_name, m.column_family, h.table_name AS hbase_table_name" +
                    "  FROM mfg_system_table t, mfg_system_table_column c, mfg_system_table_to_hbase_column_mapping m, mfg_system_table_to_hbase h, mfg_system s" +
                    " WHERE t.id = c.table_id AND c.id = m.col_id AND h.table_id = t.id AND t.system_id = s.id AND s.name = ?";
    public Map<String, List<TableToHbaseColumnsMapping>> getTableColumnColumnFamilyMappingBySystem(String systemName) throws SQLException {
        Connection conn = null;
        List<TableToHbaseColumnsMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_COLUMNS_MAPPED_HBASE_COLUMNS_BY_SYSTEM);
            ) {
                pstmt.setString(1, systemName);
                ResultSet rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    TableToHbaseColumnsMapping mapping = BeanBuilders.buildTableColumnsHbaseMapping(rs);
                    entities.add(mapping);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }

        if (entities != null && !entities.isEmpty()) {
            Map<String, List<TableToHbaseColumnsMapping>> map = new HashMap<>();
            for (TableToHbaseColumnsMapping entity : entities) {
                //logger.debug("{}", entity);
                if (!map.containsKey(entity.getTableName())) {
                    map.put(entity.getTableName(), new ArrayList<>());
                }
                map.get(entity.getTableName()).add(entity);
            }
            return map;
        }
        return null;
    }

    private static final String META_FETCH_TABLE_COLUMNS_MAPPED_HBASE_COLUMNS_BY_SYSTEM =
            "SELECT c.col_data_type, c.col_name, m.mapped_col_name, m.column_family, t.table_name, h.table_name AS hbase_table_name" +
                    "  FROM mfg_system_table t, mfg_system_table_column c, mfg_system_table_to_hbase h, mfg_system_table_to_hbase_column_mapping m, mfg_system s" +
                    " WHERE t.id = c.table_id AND c.id = m.col_id AND h.table_id = t.id AND t.system_id = s.id AND s.name = ?";
    public Map<String, Map<String, MetadataColumnBean>> getMetaMappingTableColumnColumnFamilyMappingBySystem(String systemName) throws SQLException {

        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(META_FETCH_TABLE_COLUMNS_MAPPED_HBASE_COLUMNS_BY_SYSTEM );) {
                pstmt.setString(1, systemName);
                ResultSet rs = pstmt.executeQuery();

                Map<String, Map<String, MetadataColumnBean>> tables = new HashMap<>();

                while (rs.next()) {
                    MetadataColumnBean mapping = BeanBuilders.buildMetaTableToHbaseColumnsMapping(rs);

                    String tableName = rs.getString("table_name");
                    if (tableName == null) continue;
                    Map<String, MetadataColumnBean> cols = tables.get(tableName);
                    if (cols == null){
                        cols = new HashMap<>();
                    }
                    cols.put(rs.getString("col_name"), mapping);
                    tables.put(tableName, cols);
                }

                return tables;
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);

        }
    }


    //UNIQUE (id, col_id, short_col_name),
    public List<Tuple3<Integer, Integer, String>> getColumnIdsByNames(List<Integer> columnIds) throws SQLException {
        StringBuilder builder = new StringBuilder("SELECT id, col_id, mapped_col_name FROM mfg_system_table_to_hbase_column_mapping WHERE col_id IN (");
        for (int i = 0; i < columnIds.size(); i++) {
            int cid = columnIds.get(i);
            builder.append(cid);
            if (i != columnIds.size() - 1) {
                builder.append(",");
            }
        }
        builder.append(")");
        logger.debug("{}", builder.toString());

        Connection conn = null;
        ResultSet rs = null;
        List<Tuple3<Integer, Integer, String>> columnIdTuple3s = new ArrayList<>();

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement()) {
                rs = stmt.executeQuery(builder.toString());
                while (rs.next()) {
                    int hcid = rs.getInt(1);
                    int cid = rs.getInt(2);
                    String shortColName = rs.getString(3);
                    columnIdTuple3s.add(Tuple3.of(hcid, cid, shortColName));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return columnIdTuple3s;
    }

//    INSERT INTO mfg_system_table_column_hbase_mapping (col_id, short_col_name, column_family, created_by) VALUES (18, "wareHouseCode",            'geo', 'sdu');
    private static final String INSERT_Q = "INSERT INTO mfg_system_table_to_hbase_column_mapping (col_id, mapped_col_name, column_family, created_by) VALUES (?, ?, ?, ?)";
    public List<Tuple3<Integer, Integer, String>> persist(String tableName, List<Tuple3<Integer, String, String>> columnIdNameCFTuple3s, String username) throws SQLException, MfgException {
        Connection conn = null;
        ResultSet rs = null;
        //mapping Tuple3 of <row id, col_id, col_name>
        List<Tuple3<Integer, Integer, String>> columnIdsNameTuple3s = new ArrayList<>();

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                for (Tuple3<Integer, String, String> tuple : columnIdNameCFTuple3s) {
                    pstmt.setInt(1, tuple.getFirst());
                    pstmt.setString(2, tuple.getSecond());
                    pstmt.setString(3, tuple.getThird());
                    pstmt.setString(4, username);
                    pstmt.addBatch();
                }

                int[] updateCounts = pstmt.executeBatch();
                rs = pstmt.getGeneratedKeys();
                int pos = 0;

                while (rs.next()) {
                    while (!(updateCounts[pos] >= 0 || updateCounts[pos] == Statement.SUCCESS_NO_INFO) && pos < columnIdNameCFTuple3s.size()) {
                        pos++;
                    }

                    if (updateCounts[pos] >= 0 || updateCounts[pos] == Statement.SUCCESS_NO_INFO) {
                        //columnDefs.get(pos).setId(rs.getInt(1));
                        Tuple3<Integer, String, String> columnIdNameCFTuple3 = columnIdNameCFTuple3s.get(pos);
                        columnIdsNameTuple3s.add(Tuple3.of(rs.getInt(1), columnIdNameCFTuple3.getFirst(), columnIdNameCFTuple3.getSecond()));
                        pos++;
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new MfgException(String.format("Exception caught when saving mfg system table to hbase columns mapping '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return columnIdsNameTuple3s;
    }

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        try {
            SQLDatasource ds = new SQLDatasource(config);
            ds.connect();

            MfgSystemTableToHbaseColumnMappingDAO dao = new MfgSystemTableToHbaseColumnMappingDAO(ds);
            List<MfgSystemTableToHbaseColumnMapping> entities = dao.fetchAll();
            Map<String, Map<String, MetadataColumnBean>> metaTableCols =
                    dao.getMetaMappingTableColumnColumnFamilyMappingBySystem("agv111");
            logger.debug("{}", metaTableCols);
            logger.debug("{}", dao.fetchAllTableColumnsHbaseMapping());
            logger.debug("{}", dao.fetchAllTableColumnsHbaseMappingByTableName("device_status"));
            logger.debug("{}", dao.getTableColumnColumnFamilyMapping());
            logger.debug("{}", dao.getTableColumnColumnFamilyMappingBySystem("agv"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
