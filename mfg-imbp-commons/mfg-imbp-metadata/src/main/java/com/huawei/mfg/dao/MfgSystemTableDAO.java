package com.huawei.mfg.dao;

import com.google.common.base.Strings;
import com.huawei.mfg.bean.meta.MetadataColumnBean;
import com.huawei.mfg.bean.MfgSystemTable;
import com.huawei.mfg.bean.MfgSystemTableColumn;
import com.huawei.mfg.bean.MfgSystemTableColumns;
import com.huawei.mfg.bean.topology.TopoMfgSystemTable;
import com.huawei.mfg.conf.MfgJDBCDatasourceConfiguration;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.BeanBuilders;
import com.huawei.mfg.util.MfgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class MfgSystemTableDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTableDAO.class);

    public MfgSystemTableDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<MfgSystemTable> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTable> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_system_table")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTable entity = BeanBuilders.buildMfgSystemTable(rs);
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

    public MfgSystemTable fetchByTableName(String name) throws SQLException {
        Connection conn = null;
        MfgSystemTable entity = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM mfg_system_table WHERE table_name=?");
            ) {
                pstmt.setString(1, name);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    entity = BeanBuilders.buildMfgSystemTable(rs);
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

    private static final String FETCH_TABLE_COLUMNS_BY_SYSID_TABLENAME_Q =
            "SELECT a.*, c.*, a.id AS tid, c.id AS cid"+
            " FROM mfg_system_table a"+
            " LEFT JOIN mfg_system_table_column c"+
            "      ON a.id = c.table_id" +
            " WHERE a.table_name=? AND system_id = ?";
    public MfgSystemTableColumns fetchTableColumnsByTableName(int systemId, String name) throws SQLException {
        Connection conn = null;
        MfgSystemTableColumns entity = null;
        ResultSet rs = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_COLUMNS_BY_SYSID_TABLENAME_Q);
            ) {
                pstmt.setString(1, name);
                pstmt.setInt(2, systemId);
                rs = pstmt.executeQuery();

                List<MfgSystemTableColumn> columns = new ArrayList<>();

                boolean first = true;
                while (rs.next()) {
                    if (first) {
                        first = false;
                        entity = BeanBuilders.buildMfgSystemTableColumns(rs);
                        entity.setId(rs.getInt("tid"));
                        entity.setColumns(columns);
                    }

                    MfgSystemTableColumn column = BeanBuilders.buildMfgSystemTableColumn(rs);
                    if (column != null) {
                        column.setId(rs.getInt("cid"));
                        columns.add(column);
                    }
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

    private static final String FETCH_ALL_BY_SYSTEM_NAME_Q = "SELECT a.* FROM mfg_system_table a, mfg_system b"
            + " WHERE a.system_id = b.id AND b.name=?";
    public List<MfgSystemTable> fetchAllBySystemName(String name) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        List<MfgSystemTable> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_BY_SYSTEM_NAME_Q);
            ) {
                pstmt.setString(1, name);
                rs = pstmt.executeQuery();
                entities = new ArrayList<>();

                while (rs.next()) {
                    MfgSystemTable entity = BeanBuilders.buildMfgSystemTable(rs);
                    if (entity != null) entities.add(entity);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return entities;
    }

    private static final String FETCH_ALL_TABLE_COLUMNS_BY_SYSTEM_NAME_Q =
        " SELECT a.id tid,"
        + " a.system_id, "
        + " a.table_name, "
        + " a.sample_table_name, "
        + " a.partition_keys, "
        + " a.primary_keys, "
        + " a.unique_keys, "
        + " a.source, "
        + " a.enabled, "
        + " a.created_by t_created_by, "
        + " a.updated_by t_updated_by, "
        + " a.last_updated t_last_updated, "
        + " a.created t_created, "
        + " c.id cid, "
        + " c.table_id, "
        + " c.col_name, "
        + " c.col_data_type, "
        + " c.nullable, "
        + " c.created_by c_created_by, "
        + " c.updated_by c_updated_by, "
        + " c.last_updated c_last_updated, "
        + " c.col_format, "
        + " c.col_pattern, "
        + " c.column_size, "
        + " c.created c_created "
        + "  FROM mfg_system_table a"
        + "  LEFT JOIN mfg_system_table_column c"
        + "       ON a.id = c.table_id"
        + " INNER JOIN mfg_system b"
        + "       ON a.system_id = b.id"
        + " WHERE b.name=?"
        + " ORDER BY a.table_name";

    public List<MfgSystemTableColumns> fetchAllTableColumnsBySystemName(String name) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        List<MfgSystemTableColumns> entities = null;
        MfgSystemTableColumns entity = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_TABLE_COLUMNS_BY_SYSTEM_NAME_Q);
            ) {
                pstmt.setString(1, name);
                rs = pstmt.executeQuery();
                entities = new ArrayList<>();

                String prevTableName = null;
                List<MfgSystemTableColumn> columns = null;
                while (rs.next()) {
                    String tableName = rs.getString("table_name");

                    if (prevTableName == null || !tableName.equalsIgnoreCase(prevTableName)) {
                        prevTableName = tableName;
//                        System.err.println(prevTableName);

                        entity = buildMfgSystemTableColumns(rs);
                        columns = new ArrayList<>();
                        entity.setColumns(columns);
                        entities.add(entity);
                    }

                    MfgSystemTableColumn column = buildMfgSystemTableColumn(rs);
                    if (column.getId() > 0) {
                        columns.add(column);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return entities;
    }

    private static final String FETCH_ALL_TABLE_META_COLUMNS_BY_SYSTEM_NAME_Q =
            " SELECT a.id tid,"
                    + " a.system_id, "
                    + " a.table_name, "
                    + " a.sample_table_name, "
                    + " a.partition_keys, "
                    + " a.primary_keys, "
                    + " a.unique_keys, "
                    + " a.source, "
                    + " a.enabled, "
                    + " a.created_by t_created_by, "
                    + " a.updated_by t_updated_by, "
                    + " a.last_updated t_last_updated, "
                    + " a.created t_created, "
                    + " c.id  cid, "
                    + " c.table_id, "
                    + " c.col_name, "
                    + " c.col_data_type, "
                    + " c.nullable, "
                    + " c.created_by c_created_by, "
                    + " c.updated_by c_updated_by, "
                    + " c.last_updated c_last_updated, "
                    + " c.col_format, "
                    + " c.col_pattern, "
                    + " c.created c_created "
                    + "  FROM mfg_system_table a"
                    + "  LEFT JOIN mfg_system_table_column c"
                    + "       ON a.id = c.table_id"
                    + " INNER JOIN mfg_system b"
                    + "       ON a.system_id = b.id"
                    + " WHERE b.name=?"
                    + " ORDER BY a.table_name";
    @Deprecated
    public Map<String, Map<String, Object>> fetchAllTableMetaColumnsBySystemName(String name) throws SQLException {
        ResultSet rs = null;
        Connection conn = null;

        Map<String, Map<String, Object>> table = new HashMap<>();

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_TABLE_META_COLUMNS_BY_SYSTEM_NAME_Q);) {

                pstmt.setString(1, name);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String tableName = rs.getString("table_name");

                    Map<String, Object> columns = table.get(tableName);
                    if(columns == null){
                        columns = new HashMap<>();
                        MfgSystemTableColumns tableMetadata = buildMfgSystemTableColumns(rs);
                        columns.put("tableMetadata", tableMetadata);
                    }
                    MetadataColumnBean column = buildMfgTableMetaColumn(rs);
                    if(column != null) {
                        columns.put(column.getColumnName(), column);
                    }
                    table.put(tableName, columns);
                }
            }
        } finally {
            closeAllQuietly(rs, conn, null);
        }
        return table;
    }

    private static final String FETCH_ALL_TABLE_META_COLUMNS_BY_SYSTEM_ID_Q =
                      "SELECT a.id tid,"
                    + "       a.system_id, "
                    + "       a.table_name, "
                    + "       a.sample_table_name, "
                    + "       a.partition_keys, "
                    + "       a.primary_keys, "
                    + "       a.unique_keys, "
                    + "       a.source, "
                    + "       a.enabled, "
                    + "       a.created_by t_created_by, "
                    + "       a.updated_by t_updated_by, "
                    + "       a.last_updated t_last_updated, "
                    + "       a.created t_created, "
                    + "       c.id  cid, "
                    + "       c.table_id, "
                    + "       c.col_name, "
                    + "       c.col_data_type, "
                    + "       c.nullable, "
                    + "       c.column_size, "
                    + "       c.created_by c_created_by, "
                    + "       c.updated_by c_updated_by, "
                    + "       c.last_updated c_last_updated, "
                    + "       c.col_format, "
                    + "       c.col_pattern, "
                    + "       c.created c_created "
                    + "  FROM mfg_system_table a"
                    + "       LEFT JOIN mfg_system_table_column c ON a.id = c.table_id"
                    + " WHERE a.enabled = 'yes' AND a.system_id=?"
                    + " ORDER BY a.table_name";
    public Map<String, Map<String, Object>> fetchAllTableMetaColumnsBySystemId(int sysId) throws SQLException {
        ResultSet rs = null;
        Connection conn = null;

        Map<String, Map<String, Object>> table = new HashMap<>();

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_TABLE_META_COLUMNS_BY_SYSTEM_ID_Q);) {

                pstmt.setInt(1, sysId);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String tableName = rs.getString("table_name");

                    Map<String, Object> columns = table.get(tableName);
                    if(columns == null){
                        columns = new HashMap<>();
                        MfgSystemTableColumns tableMetadata = buildMfgSystemTableColumns(rs);
                        columns.put("tableMetadata", tableMetadata);
                    }
                    MetadataColumnBean column = buildMfgTableMetaColumn(rs);
                    if(column != null) {
                        columns.put(column.getColumnName(), column);
                    }
                    table.put(tableName, columns);
                }
            }
        } finally {
            closeAllQuietly(rs, conn, null);
        }
        return table;
    }

    public Map<String, TopoMfgSystemTable> fetchAllTableMetaColumnPojosBySystemId(int sysId) throws SQLException {
        ResultSet rs = null;
        Connection conn = null;

        Map<String, Map<String, Object>> table = new HashMap<>();
        Map<String, TopoMfgSystemTable> map = new HashMap<>();

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_TABLE_META_COLUMNS_BY_SYSTEM_ID_Q);) {
                pstmt.setInt(1, sysId);
                rs = pstmt.executeQuery();

//                String prevTableName = null;
                while (rs.next()) {
                    String tableName = rs.getString("table_name");
                    TopoMfgSystemTable topoMfgSystemTable = map.get(tableName);
                    if (topoMfgSystemTable == null) {
                        topoMfgSystemTable = new TopoMfgSystemTable();
                        BeanBuilders.buildMfgSystemTable(topoMfgSystemTable, rs);
                        map.put(tableName, topoMfgSystemTable);
                    }

                    MfgSystemTableColumn column = BeanBuilders.buildMfgSystemTableColumn(rs);
                    column.setId(rs.getInt("cid"));
                    topoMfgSystemTable.addColumn(column);
                }
            }
        } finally {
            closeAllQuietly(rs, conn, null);
        }
        return map;
    }

    private static MfgSystemTableColumns buildMfgSystemTableColumns(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableColumns entity = new MfgSystemTableColumns();
        entity.setSystemId(rs.getInt("system_id"));
        entity.setTableName(rs.getString("table_name"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));
        entity.setSource(rs.getString("source"));
        entity.setPartitionKeys(rs.getString("partition_keys"));
        entity.setPrimaryKeys(rs.getString("primary_keys"));
        entity.setUniqueKeys(rs.getString("unique_keys"));
        entity.setSampleTableName(rs.getString("sample_table_name"));

        entity.setId(rs.getInt("tid"));
        entity.setCreatedBy(rs.getString("t_created_by"));
        entity.setUpdatedBy(rs.getString("t_updated_by"));
        entity.setCreated(rs.getTimestamp("t_created"));
        entity.setLastUpdated(rs.getTimestamp("t_last_updated"));

        return entity;
    }

    private static MfgSystemTableColumn buildMfgSystemTableColumn(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableColumn entity = new MfgSystemTableColumn();
        entity.setTableId(rs.getInt("table_id"));
        entity.setColumnName(rs.getString("col_name"));
        entity.setColumnDataType(rs.getString("col_data_type"));

        entity.setId(rs.getInt("cid"));
        entity.setCreatedBy(rs.getString("c_created_by"));
        entity.setUpdatedBy(rs.getString("c_updated_by"));
        entity.setCreated(rs.getTimestamp("c_created"));
        entity.setLastUpdated(rs.getTimestamp("c_last_updated"));

        String nullable = rs.getString("nullable");
        if (!Strings.isNullOrEmpty(nullable)) {
            entity.setNullable(nullable.equals("yes"));
        }

        entity.setColumnFormat(rs.getString("col_format"));
        entity.setColumnPattern(rs.getString("col_pattern"));
        entity.setColumnSize(rs.getInt("column_size"));

        return entity;
    }

    private static MetadataColumnBean buildMfgTableMetaColumn(ResultSet rs) throws SQLException {

        if (rs == null && !rs.next()) return null;
        MetadataColumnBean entity = new MetadataColumnBean();
        entity.setColumnName(rs.getString("col_name"));
        entity.setColumnDataType(rs.getString("col_data_type"));

        String nullable = rs.getString("nullable");
        if (!Strings.isNullOrEmpty(nullable)) {
            entity.setNullable(nullable.equals("yes"));
        }

        entity.setColumnFormat(rs.getString("col_format"));
        entity.setColumnPattern(rs.getString("col_pattern"));

        return entity;

    }

    private static final String FETCH_ALL_BY_SYSTEM_SOURCE_Q = "SELECT a.* FROM mfg_system_table a, mfg_system b"
            + " WHERE a.system_id = b.id AND b.name=? AND a.source=?";
    public List<MfgSystemTable> fetchAllSystemTablesBySystemSource(String system, String source) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        List<MfgSystemTable> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_ALL_BY_SYSTEM_SOURCE_Q);
            ) {
                pstmt.setString(1, system);
                pstmt.setString(2, source);
                rs = pstmt.executeQuery();
                entities = new ArrayList<>();

                while (rs.next()) {
                    MfgSystemTable entity = BeanBuilders.buildMfgSystemTable(rs);
                    if (entity != null) entities.add(entity);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return entities;
    }

    public Integer getTableIdByName(String name) throws SQLException {
        return this.getIdByName("mfg_system_table", "table_name", name);
    }

    private static final String INSERT_Q = "INSERT INTO mfg_system_table (system_id, table_name, sample_table_name, source, enabled, created_by, primary_keys, unique_keys) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public int persist(int systemId, MfgSystemTable mfgSystemTable, String username)
            throws MfgException
    {
        return this.persist(systemId,
                mfgSystemTable.getTableName(),
                mfgSystemTable.getSampleTableName(),
                mfgSystemTable.getSource(),
                mfgSystemTable.isEnabled(),
                username,
                mfgSystemTable.getPrimaryKeys(),
                mfgSystemTable.getUniqueKeys());
    }

    public int persist(int systemId, String tableName, String sampleTableName, String source, boolean enabled, String username, String primaryKeys, String uniqueKeys)
            throws MfgException
    {
        Connection conn = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, systemId);
                pstmt.setString(2, tableName);
                pstmt.setString(3, sampleTableName);
                pstmt.setString(4, source);
                pstmt.setString(5, (enabled?"yes":"no"));
                pstmt.setString(6, username);
                pstmt.setString(7, primaryKeys);
                pstmt.setString(8, uniqueKeys);
                pstmt.executeUpdate();
                rs = pstmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    id = rs.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg_system_table '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return id;
    }

    private static final String UPDATE_Q = "UPDATE mfg_system_table " +
                        "SET table_name = ?, sample_table_name = ?, source = ?, enabled = ?, primary_keys = ?," +
                        " unique_keys = ?, last_updated = ?, updated_by = ?" +
                        " WHERE id = ?";
    public void update(MfgSystemTable mfgSystemTable, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                pstmt.setString(1, mfgSystemTable.getTableName());
                pstmt.setString(2, mfgSystemTable.getSampleTableName());
                pstmt.setString(3, mfgSystemTable.getSource());
                pstmt.setString(4, (mfgSystemTable.isEnabled()?"yes":"no"));
                pstmt.setString(5, mfgSystemTable.getPrimaryKeys());
                pstmt.setString(6, mfgSystemTable.getUniqueKeys());
                pstmt.setTimestamp(7, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(8, username);
                pstmt.setInt(9, mfgSystemTable.getId());
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when saving mfg system table '%s'", mfgSystemTable.getTableName()), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteById(int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            String q = "DELETE FROM mfg_system_table WHERE id = " + id;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting mfg_system_table with id '%s'", id), e);
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

            MfgSystemTableDAO dao = new MfgSystemTableDAO(ds);
//            List<MfgSystemTable> entities = dao.fetchAll();
//            logger.debug("{}", entities);

//            logger.debug("{}", dao.fetchByTableName("device_status"));
//            logger.debug("{}", dao.fetchByTableName("person"));
//            logger.debug("{}", dao.fetchAllBySystemName("agv"));
//            logger.debug("{}", dao.getTableIdByName("device_status"));
//            logger.debug("{}", dao.getTableIdByName("device_skill"));
//            logger.debug("{}", dao.getTableIdByName("person"));

//            logger.debug("{}", dao.fetchTableColumnsByTableName("'t_ai_base_area'"));
//            Map<String, Map<String, Object>> metaColumns = dao.fetchAllTableMetaColumnsBySystemName("aoi_single_component_image");
//            logger.info(metaColumns.toString());

//            List<MfgSystemTableColumns> columns = dao.fetchAllTableColumnsBySystemName("aoi_single_component_image");
            List<MfgSystemTableColumns> columns = dao.fetchAllTableColumnsBySystemName("bbb");
            logger.info(columns.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
