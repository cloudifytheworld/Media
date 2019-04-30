package com.huawei.mfg.dao;

import com.huawei.mfg.bean.*;
import com.huawei.mfg.bean.meta.MetadataColumnBean;
import com.huawei.mfg.bean.meta.MetadataTableCassandraBean;
import com.huawei.mfg.bean.topology.TopoMfgSystemTableToCassandra;
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

@Deprecated
@SuppressWarnings("Duplicates")
/**
 * @deprecated @see(ProductTableColumnToCassandraColumnDAO)
 */
public class MfgSystemTableToCassandraColumnMappingDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(MfgSystemTableToCassandraColumnMappingDAO.class);

    public MfgSystemTableToCassandraColumnMappingDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }


    public List<MfgSystemTableToCassandraColumnMapping> fetchAll() throws SQLException {
        Connection conn = null;
        List<MfgSystemTableToCassandraColumnMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mfg_system_table_to_cassandra_column_mapping")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    MfgSystemTableToCassandraColumnMapping entity = BeanBuilders.buildMfgSystemTableToCassandraColumnMapping(rs);
                    if (entity != null) entities.add(entity);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    private static final String FETCH_ALL_TABLE_TO_CASSANDRA_COLUMN_MAPPING_Q =
//                    "SELECT t.table_name," +
//                    "       c.col_name," +
//                    "       c.col_data_type," +
//                    "       m.mapped_col_name," +
//                    "       h.table_name AS cass_table_name," +
//                    "       h.key_space," +
//                    "       h.partition_keys," +
//                    "       h.clustering_keys," +
//                    "       m.id AS cass_col_id," +
//                    "       c.id AS col_id," +
//                    "       m.cass_table_id" +
//                    "  FROM mfg_system_table t, " +
//                    "       mfg_system_table_column c," +
//                    "       mfg_system_table_to_cassandra_column_mapping m," +
//                    "       mfg_system_table_to_cassandra h" +
//                    " WHERE t.id = c.table_id" +
//                    "   AND c.id = m.col_id" +
//                    "   AND h.table_id = t.id";
                    "SELECT tbl.*, m.mapped_col_name, m.id AS cass_col_id, m.ptable_col_to_cass_id AS col_cassandra_ref_id, j.id AS ref_id, j.cass_table_id " +
                    "  FROM ( " +
                    "        SELECT t.table_name, " +
                    "               c.col_name, " +
                    "               c.col_data_type, " +
                    "               h.table_name AS cass_table_name, " +
                    "               h.key_space, " +
                    "               h.partition_keys, " +
                    "               h.clustering_keys, " +
                    "               c.id AS col_id, " +
                    "               h.id AS cass_id " +
                    "          FROM mfg_system_table t,  " +
                    "               mfg_system_table_column c, " +
                    "               mfg_system_table_to_cassandra h " +
                    "         WHERE t.id = c.table_id " +
                    "           AND h.table_id = t.id " +
                    "        ) tbl " +
                    "        LEFT JOIN product_table_column_to_cassandra j ON j.cass_table_id = tbl.cass_id AND j.col_id = tbl.col_id   " +
                    "        LEFT JOIN product_table_column_to_cassandra_column_mapping m ON j.id = m.ptable_col_to_cass_id  ";
    public List<TableToCassandraColumnsMapping> fetchAllTableColumnsCassandraMapping() throws SQLException {
        Connection conn = null;
        List<TableToCassandraColumnsMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(FETCH_ALL_TABLE_TO_CASSANDRA_COLUMN_MAPPING_Q);
                entities = new ArrayList<>();
                while (rs.next()) {
                    TableToCassandraColumnsMapping mapping = BeanBuilders.buildTableToCassandraColumnsMapping(rs);
                    entities.add(mapping);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    private static final String FETCH_TABLE_TO_CASSANDRA_COLUMN_MAPPING_BY_TABLE_NAME_Q =
//                    "SELECT t.table_name," +
//                    "        c.col_name," +
//                    "        c.col_data_type," +
//                    "        m.mapped_col_name," +
//                    "        h.table_name AS cass_table_name," +
//                    "        h.key_space," +
//                    "        h.partition_keys," +
//                    "        h.clustering_keys," +
//                    "        m.id AS cass_col_id," +
//                    "        c.id AS col_id," +
//                    "        m.cass_table_id" +
//                    "  FROM mfg_system_table t," +
//                    "       mfg_system_table_column c," +
//                    "       mfg_system_table_to_cassandra_column_mapping m," +
//                    "       mfg_system_table_to_cassandra h" +
//                    " WHERE t.id = c.table_id " +
//                    "   AND c.id = m.col_id " +
//                    "   AND h.table_id = t.id " +
//                    "   AND t.table_name = ?";
                    "SELECT tbl.*, m.mapped_col_name, m.id AS cass_col_id, m.ptable_col_to_cass_id AS col_cassandra_ref_id, j.id AS ref_id, j.cass_table_id " +
                    "  FROM ( " +
                    "        SELECT t.table_name, " +
                    "               c.col_name, " +
                    "               c.col_data_type, " +
                    "               h.table_name AS cass_table_name, " +
                    "               h.key_space, " +
                    "               h.partition_keys, " +
                    "               h.clustering_keys, " +
                    "               c.id AS col_id, " +
                    "               h.id AS cass_id " +
                    "          FROM mfg_system_table t,  " +
                    "               mfg_system_table_column c, " +
                    "               mfg_system_table_to_cassandra h " +
                    "         WHERE t.id = c.table_id " +
                    "           AND h.table_id = t.id " +
                    "           AND t.table_name = ?" +
                    "        ) tbl " +
                    "        LEFT JOIN product_table_column_to_cassandra j ON j.cass_table_id = tbl.cass_id AND j.col_id = tbl.col_id   " +
                    "        LEFT JOIN product_table_column_to_cassandra_column_mapping m ON j.id = m.ptable_col_to_cass_id  ";
    public List<TableToCassandraColumnsMapping> fetchAllTableCassandraColumnMappingsByTableName(String tableName) throws SQLException {
        Connection conn = null;
        List<TableToCassandraColumnsMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_TO_CASSANDRA_COLUMN_MAPPING_BY_TABLE_NAME_Q);) {
                pstmt.setString(1, tableName);
                ResultSet rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    TableToCassandraColumnsMapping mapping = BeanBuilders.buildTableToCassandraColumnsMapping(rs);
                    entities.add(mapping);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    private static final String FETCH_TABLE_TO_CASSANDRA_COLUMN_MAPPING_BY_CASSANDRA_TABLE_ID_Q =
//            "SELECT t.table_name," +
//                    "        c.col_name," +
//                    "        c.col_data_type," +
//                    "        m.mapped_col_name," +
//                    "        h.table_name AS cass_table_name," +
//                    "        h.key_space," +
//                    "        h.partition_keys," +
//                    "        h.clustering_keys," +
//                    "        m.id AS cass_col_id," +
//                    "        c.id AS col_id," +
//                    "        m.cass_table_id" +
//                    "  FROM mfg_system_table t," +
//                    "       mfg_system_table_column c," +
//                    "       mfg_system_table_to_cassandra_column_mapping m," +
//                    "       mfg_system_table_to_cassandra h" +
//                    " WHERE t.id = c.table_id " +
//                    "   AND c.id = m.col_id " +
//                    "   AND h.table_id = t.id " +
//                    "   AND m.cass_table_id = ?";
            "SELECT tbl.*, m.mapped_col_name, m.id AS cass_col_id, m.ptable_col_to_cass_id AS col_cassandra_ref_id, j.id AS ref_id, j.cass_table_id " +
                    "  FROM ( " +
                    "         SELECT t.table_name, " +
                    "                c.col_name, " +
                    "                c.col_data_type, " +
                    "                h.table_name AS cass_table_name, " +
                    "                h.key_space, " +
                    "                h.partition_keys, " +
                    "                h.clustering_keys, " +
                    "                c.id AS col_id, " +
                    "                h.id AS cass_id " +
                    "           FROM mfg_system_table t, " +
                    "                mfg_system_table_column c, " +
                    "                mfg_system_table_to_cassandra h " +
                    "          WHERE t.id = c.table_id  " +
                    "            AND h.table_id = t.id  " +
                    "        ) tbl " +
                    "        LEFT JOIN product_table_column_to_cassandra j ON j.cass_table_id = tbl.cass_id AND j.col_id = tbl.col_id   " +
                    "        LEFT JOIN product_table_column_to_cassandra_column_mapping m ON j.id = m.ptable_col_to_cass_id  " +
                    "  WHERE cass_table_id = ?";
    public List<TableToCassandraColumnsMapping> fetchAllTableCassandraColumnMappingsByCassandraTableId(int casssandraId) throws SQLException {
        Connection conn = null;
        List<TableToCassandraColumnsMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_TO_CASSANDRA_COLUMN_MAPPING_BY_CASSANDRA_TABLE_ID_Q);) {
                pstmt.setInt(1, casssandraId);
                ResultSet rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    TableToCassandraColumnsMapping mapping = BeanBuilders.buildTableToCassandraColumnsMapping(rs);
                    entities.add(mapping);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    private static final String FETCH_TABLE_COLUMNS_MAPPED_CASSANDRA_COLUMNS_BY_SYSTEM =
//                    "SELECT t.table_name," +
//                    "       c.col_name," +
//                    "       c.col_data_type," +
//                    "       m.mapped_col_name," +
//                    "       h.table_name AS cass_table_name," +
//                    "       h.key_space," +
//                    "       h.partition_keys," +
//                    "       h.clustering_keys," +
//                    "       m.id AS cass_col_id," +
//                    "       c.id AS col_id," +
//                    "       m.cass_table_id" +
//                    "  FROM mfg_system s," +
//                    "       mfg_system_table t," +
//                    "       mfg_system_table_column c," +
//                    "       mfg_system_table_to_cassandra_column_mapping m," +
//                    "       mfg_system_table_to_cassandra h " +
//                    " WHERE t.system_id = s.id " +
//                    "   AND t.id = h.table_id " +
//                    "   AND t.id = c.table_id " +
//                    "   AND c.id = m.col_id " +
//                    "   AND s.name = ?";
            "SELECT tbl.*, m.mapped_col_name, m.id AS cass_col_id, m.ptable_col_to_cass_id AS col_cassandra_ref_id, j.id AS ref_id, j.cass_table_id " +
            "  FROM ( " +
            "        SELECT t.table_name, " +
            "               c.id AS col_id, " +
            "               c.col_name, " +
            "               c.col_data_type, " +
            "               h.table_name AS cass_table_name, " +
            "               h.key_space, " +
            "               h.partition_keys, " +
            "               h.clustering_keys, " +
            "               h.id AS cass_id " +
            "          FROM mfg_system s, " +
            "               mfg_system_table t, " +
            "               mfg_system_table_column c, " +
            "               mfg_system_table_to_cassandra h " +
            "         WHERE t.system_id = s.id " +
            "           AND t.id = h.table_id " +
            "           AND t.id = c.table_id " +
            "           AND s.name = ? " +
            "      ) tbl " +
            "      LEFT JOIN product_table_column_to_cassandra j ON j.cass_table_id = tbl.cass_id AND j.col_id = tbl.col_id " +
            "      LEFT JOIN product_table_column_to_cassandra_column_mapping m ON j.id = m.ptable_col_to_cass_id";
    public Map<String, List<TableToCassandraColumnsMapping>> getTableColumnColumnFamilyMappingBySystem(String systemName) throws SQLException {
        Connection conn = null;
        List<TableToCassandraColumnsMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_COLUMNS_MAPPED_CASSANDRA_COLUMNS_BY_SYSTEM);) {
                pstmt.setString(1, systemName);
                ResultSet rs = pstmt.executeQuery();

                entities = new ArrayList<>();
                while (rs.next()) {
                    TableToCassandraColumnsMapping mapping = BeanBuilders.buildTableToCassandraColumnsMapping(rs);
                    entities.add(mapping);
                }
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }

        if (entities != null && !entities.isEmpty()) {
            Map<String, List<TableToCassandraColumnsMapping>> map = new HashMap<>();
            for (TableToCassandraColumnsMapping entity : entities) {
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

    private static final String META_FETCH_TABLE_COLUMNS_MAPPED_CASSANDRA_COLUMNS_BY_SYSTEM =
//                    "       mfg_system_table_to_cassandra_column_mapping m" +
                    "SELECT tbl.*, m.mapped_col_name " +
                    "  FROM (" +
                    "         SELECT c.col_name," +
                    "                c.col_data_type," +
                    "                c.id AS col_id," +
                    "                t.table_name," +
                    "                h.key_space," +
                    "                h.table_name AS ctable_name," +
                    "                h.partition_keys," +
                    "                h.clustering_keys," +
                    "                h.id AS cass_id" +
                    "           FROM mfg_system s," +
                    "                mfg_system_table t," +
                    "                mfg_system_table_column c," +
                    "                mfg_system_table_to_cassandra h " +
                    "          WHERE t.system_id = s.id " +
                    "            AND t.id = c.table_id " +
                    "            AND t.id = h.table_id " +
                    "            AND s.name = ?" +
                    "       ) tbl" +
                    "       LEFT JOIN product_table_column_to_cassandra j ON j.cass_table_id = tbl.cass_id AND j.col_id = tbl.col_id  " +
                    "       LEFT JOIN product_table_column_to_cassandra_column_mapping m ON j.id = m.ptable_col_to_cass_id  ";
//    @Deprecated
//    public Map<String, Map<String, Object>> getMetaMappingTableColumnsBySystem(String systemName) throws SQLException {
//        Connection conn = null;
//        try {
//            conn = this.sqlDatasource.getConnection();
//            try (PreparedStatement pstmt = conn.prepareStatement(META_FETCH_TABLE_COLUMNS_MAPPED_CASSANDRA_COLUMNS_BY_SYSTEM);) {
//                pstmt.setString(1, systemName);
//                ResultSet rs = pstmt.executeQuery();
//
//                Map<String, Map<String, Object>> tables = new HashMap<>();
//
//                while (rs.next()) {
//                    MetadataColumnBean mapping = BeanBuilders.buildMetaTableToCassandraColumnsMapping(rs);
//
//                    String tableName = rs.getString("table_name");
//                    if(tableName == null) continue;
//                    Map<String, Object> cols = tables.get(tableName);
//                    if(cols == null){
//                        cols = new HashMap<>();
//                        MetadataTableCassandraBean tableMetadata= BeanBuilders.buildCassandraMetadata(rs);
//                        cols.put("tableMetadata", tableMetadata);
//                    }
//                    cols.put(rs.getString("col_name"), mapping);
//                    tables.put(tableName, cols);
//                }
//
//                return tables;
//            }
//        }
//        finally {
//            this.closeAllQuietly(null, conn, null);
//        }
//    }

    private static final String META_FETCH_TABLE_COLUMNS_MAPPED_CASSANDRA_COLUMNS_BY_SYSTEM_ID =
                "SELECT tbl.col_name, " +
                "       tbl.col_data_type, " +
                "       tbl.table_name, " +
//                "       tbl.cass_table_name, " +
                "       tbl.cass_id, " +
                "       tbl.table_id, " +
                "       tbl.key_space, " +
                "       tbl.partition_keys, " +
                "       tbl.clustering_keys, " +
                "       tbl.ctable_name, " +
                "       tbl.enabled, " +
                "       tbl.description, " +
                "       m.ptable_col_to_cass_id, " +
                "       m.description AS col_description, " +
                "       m.mapped_col_name " +
                "  FROM ( " +
                "         SELECT t.table_name, " +
                "                col.id AS col_id, " +
                "                col.col_name, " +
                "                col.col_data_type, " +
                "                cass.id AS cass_id, " +
//                "                cass.table_name AS cass_table_name, " +
                "                cass.table_id, " +
                "                cass.key_space, " +
                "                cass.partition_keys, " +
                "                cass.clustering_keys, " +
                "                cass.enabled, " +
                "                cass.description, " +
                "                cass.table_name AS ctable_name " +
                "           FROM mfg_system_table t, " +
                "                mfg_system_table_column col, " +
                "                mfg_system_table_to_cassandra cass " +
                "          WHERE t.id = col.table_id " +
                "            AND t.enabled = 'yes' " +
                "            AND cass.table_id = t.id " +
                "            AND t.system_id = ? " +
                "       ) tbl " +
                "       LEFT JOIN product_table_column_to_cassandra j ON j.cass_table_id = tbl.cass_id AND j.col_id = tbl.col_id  " +
                "       LEFT JOIN product_table_column_to_cassandra_column_mapping m ON j.id = m.ptable_col_to_cass_id  ";
//    public Map<String, Map<String, Object>> getMetaMappingTableColumnsBySystemId(int sysId) throws SQLException {
//        Connection conn = null;
//        try {
//            conn = this.sqlDatasource.getConnection();
//            try (PreparedStatement pstmt = conn.prepareStatement(META_FETCH_TABLE_COLUMNS_MAPPED_CASSANDRA_COLUMNS_BY_SYSTEM_ID);) {
//                pstmt.setInt(1, sysId);
//                ResultSet rs = pstmt.executeQuery();
//
//                Map<String, Map<String, Object>> tables = new HashMap<>();
//
//                while (rs.next()) {
//                    MetadataColumnBean mapping = BeanBuilders.buildMetaTableToCassandraColumnsMapping(rs);
//
//                    String tableName = rs.getString("table_name");
//                    if (tableName == null) continue;
//                    Map<String, Object> cols = tables.get(tableName);
//                    if (cols == null) {
//                        cols = new HashMap<>();
//                        MetadataTableCassandraBean tableMetadata= BeanBuilders.buildCassandraMetadata(rs);
//                        cols.put("tableMetadata", tableMetadata);
//                    }
//                    if(mapping != null) {
//                        cols.put(rs.getString("col_name"), mapping);
//                    }
//                    tables.put(tableName, cols);
//                }
//
//                return tables;
//            }
//        }
//        finally {
//            this.closeAllQuietly(null, conn, null);
//        }
//    }

    @Deprecated
    public Map<String, TopoMfgSystemTableToCassandra> getMetaMappingTableColumnPojosBySystemId(int sysId) throws SQLException {
        Connection conn = null;
        Map<String, TopoMfgSystemTableToCassandra> map = new HashMap<>();

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(META_FETCH_TABLE_COLUMNS_MAPPED_CASSANDRA_COLUMNS_BY_SYSTEM_ID);) {
                pstmt.setInt(1, sysId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String tableName = rs.getString("table_name");
                    TopoMfgSystemTableToCassandra topoMfgSystemTableToCassandra = map.get(tableName);
                    if (topoMfgSystemTableToCassandra == null) {
                        topoMfgSystemTableToCassandra = new TopoMfgSystemTableToCassandra();
                        buildMfgSystemTableToCassandra(topoMfgSystemTableToCassandra, rs);
                        topoMfgSystemTableToCassandra.setCassTableName(rs.getString("ctable_name"));
                        topoMfgSystemTableToCassandra.setId(rs.getInt("cass_id"));
                        map.put(tableName, topoMfgSystemTableToCassandra);
                    }

                    ProductTableColumnToCassandraColumn column = buildProductTabblColumnToCassandraColumn(rs);
//                    column.setId(rs.getInt("cid"));
                    if (column != null && column.getPtableColumnToCassandraId() > 0) {
                        topoMfgSystemTableToCassandra.addColumn(column);
                    }
                }

                return map;
            }
        }
        finally {
            this.closeAllQuietly(null, conn, null);
        }
    }
    public static void buildMfgSystemTableToCassandra(MfgSystemTableToCassandra entity, ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return;

        entity.setTableId(rs.getInt("table_id"));
        entity.setTableName(rs.getString("table_name"));
        entity.setKeySpace(rs.getString("key_space"));
        entity.setPartitionKeys(rs.getString("partition_keys"));
        entity.setClusteringKeys(rs.getString("clustering_keys"));
        entity.setDescription(rs.getString("description"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));
    }
    public static ProductTableColumnToCassandraColumn buildProductTabblColumnToCassandraColumn(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        ProductTableColumnToCassandraColumn entity = new ProductTableColumnToCassandraColumn();
        entity.setPtableColumnToCassandraId(rs.getInt("ptable_col_to_cass_id"));
        entity.setMappedColumnName(rs.getString("mapped_col_name"));
        entity.setDescription(rs.getString("col_description"));

        return entity;
    }

    private static final String INSERT_Q = "INSERT INTO mfg_system_table_to_cassandra_column_mapping (col_id, mapped_col_name, description, created_by, cass_table_id) VALUES (?, ?, ?, ?, ?)";
    public List<Tuple3<Integer, Integer, String>> persist(String tableName, List<MfgSystemTableToCassandraColumnMapping> tableToCassandraColumnMappings, String username) throws SQLException, MfgException {
        Connection conn = null;
        ResultSet rs = null;
        //mapping Tuple3 of <row id, col_id, col_name>
        List<Tuple3<Integer, Integer, String>> columnIdsNameTuple3s = new ArrayList<>();

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_Q, Statement.RETURN_GENERATED_KEYS)) {
                for (MfgSystemTableToCassandraColumnMapping mapping: tableToCassandraColumnMappings) {
                    pstmt.setInt(1, mapping.getColumnId());
                    pstmt.setString(2, mapping.getMappedColumnName());
                    pstmt.setString(3, mapping.getDescription());
                    pstmt.setString(4, username);
                    pstmt.setInt(5, mapping.getCassandraTableId());
                    pstmt.addBatch();
                }

                int[] updateCounts = pstmt.executeBatch();
                rs = pstmt.getGeneratedKeys();
                int pos = 0;

                while (rs.next()) {
                    while (!(updateCounts[pos] >= 0 || updateCounts[pos] == Statement.SUCCESS_NO_INFO) && pos < tableToCassandraColumnMappings.size()) {
                        pos++;
                    }

                    if (updateCounts[pos] >= 0 || updateCounts[pos] == Statement.SUCCESS_NO_INFO) {
                        MfgSystemTableToCassandraColumnMapping mapping = tableToCassandraColumnMappings.get(pos);
                        columnIdsNameTuple3s.add(Tuple3.of(rs.getInt(1), mapping.getColumnId(), mapping.getMappedColumnName()));
                        pos++;
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new MfgException(String.format("Exception caught when saving mfg system table to cassandra columns mapping '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(rs, conn, null);
        }
        return columnIdsNameTuple3s;
    }

    private static final String UPDATE_Q =
            "UPDATE mfg_system_table_to_cassandra_column_mapping " +
                    " SET mapped_col_name = ?, cass_table_id = ?, col_id = ?, description = ?," +
                    " last_updated = ?, updated_by = ?" +
                    " WHERE id = ?";
    public void update(String tableName, MfgSystemTableToCassandraColumnMapping cassandraColumnMapping, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                pstmt.setString(1, cassandraColumnMapping.getMappedColumnName());
                pstmt.setInt(2, cassandraColumnMapping.getCassandraTableId());
                pstmt.setInt(3, cassandraColumnMapping.getColumnId());
                pstmt.setString(4, cassandraColumnMapping.getDescription());
                pstmt.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(6, username);
                pstmt.setInt(7, cassandraColumnMapping.getId());
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when updating mfg_system_table_to_cassandra_column_mapping '%s'", tableName), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteByCassandraTableId(int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            String q = "DELETE FROM mfg_system_table_to_cassandra_column_mapping WHERE cass_table_id = " + id;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting mfg_system_table_to_cassandra_column_mapping with cassandra table id '%s'", id), e);
        }
        finally {
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteByColumnId(int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            String q = "DELETE FROM mfg_system_table_to_cassandra_column_mapping WHERE id = " + id;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting mfg_system_table_to_cassandra_column_mapping with id '%s'", id), e);
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

            MfgSystemTableToCassandraColumnMappingDAO dao = new MfgSystemTableToCassandraColumnMappingDAO(ds);
            List<MfgSystemTableToCassandraColumnMapping> entities = dao.fetchAll();
            List<TableToCassandraColumnsMapping> tables =
                    dao.fetchAllTableCassandraColumnMappingsByTableName("aoi_single_component_image");
            logger.info("{}", tables);
//            Map<String, Map<String, Object>> tableColumnsBySystem =
//                    dao.getMetaMappingTableColumnsBySystemId(3);
//            logger.debug("{}", tableColumnsBySystem);
            logger.debug("{}", dao.fetchAllTableColumnsCassandraMapping());
//            logger.debug("{}", dao.fetchAllTableColumnsHbaseMappingByTableName("device_status"));
//            logger.debug("{}", dao.getTableColumnColumnFamilyMapping());
//            logger.debug("{}", dao.getTableColumnColumnFamilyMappingBySystem("agv"));
            logger.debug("{}", dao.getTableColumnColumnFamilyMappingBySystem("aoi_single_component_image"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
