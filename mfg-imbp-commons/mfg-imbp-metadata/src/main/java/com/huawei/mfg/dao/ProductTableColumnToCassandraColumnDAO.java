package com.huawei.mfg.dao;

import com.huawei.mfg.bean.*;
import com.huawei.mfg.bean.meta.MetadataCassandraColumnBean;
import com.huawei.mfg.bean.meta.MetadataColumnBean;
import com.huawei.mfg.bean.meta.MetadataTableCassandraBean;
import com.huawei.mfg.bean.topology.TopoMfgSystemTableToCassandra;
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
public class ProductTableColumnToCassandraColumnDAO extends BaseDAO {
    private static Logger logger = LoggerFactory.getLogger(ProductTableColumnToCassandraColumnDAO.class);

    public ProductTableColumnToCassandraColumnDAO(SQLDatasource sqlDatasource) {
        super(sqlDatasource);
    }

    public List<ProductTableColumnToCassandraColumn> fetchAll() throws SQLException {
        Connection conn = null;
        List<ProductTableColumnToCassandraColumn> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM product_table_column_to_cassandra_column_mapping")
            ) {
                entities = new ArrayList<>();
                while (rs.next()) {
                    ProductTableColumnToCassandraColumn entity = BeanBuilders.buildProductTabblColumnToCassandraColumn(rs);
                    if (entity != null) entities.add(entity);
                }
            }
        } finally {
            this.closeAllQuietly(null, conn, null);
        }
        return entities;
    }

    private static final String FETCH_TABLE_TO_CASSANDRA_COLUMN_MAPPING_BY_CASSANDRA_TABLE_ID_Q =
                    "SELECT t.table_name," +
                    "       col.id AS col_id," +
                    "       col.col_name," +
                    "       col.col_data_type," +
                    "       cass.table_name AS cass_table_name," +
                    "       cass.key_space," +
                    "       cass.partition_keys," +
                    "       cass.clustering_keys," +
                    "       j.id AS col_cassandra_ref_id, " + //product_table_column_to_cassandra's id
                    "       j.cass_table_id, " +
                    "       m.id AS cass_col_id," +
                    "       m.mapped_col_name, " +
                    "       m.col_format, " +
                    "       m.column_size, " +
                    "       m.col_pattern, " +
                    "       m.nullable, " +
                    "       m.is_object" +
                    "  FROM mfg_system_table t," +
                    "       mfg_system_table_column col," +
                    "       mfg_system_table_to_cassandra cass," +
                    "       product_table_column_to_cassandra j, " +
                    "       product_table_column_to_cassandra_column_mapping m " +
                    " WHERE t.id = col.table_id " +
                    "   AND cass.table_id = t.id " +
                    "   AND j.cass_table_id = cass.id " +
                    "   AND j.col_id = col.id " +
                    "   AND j.id = m.ptable_col_to_cass_id " +
                    "   AND j.cass_table_id = ?";
    public List<TableToCassandraColumnsMapping> fetchAllCassandraColumnsByCassandraTableId(int casssandraTblId) throws SQLException {
        Connection conn = null;
        List<TableToCassandraColumnsMapping> entities = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(FETCH_TABLE_TO_CASSANDRA_COLUMN_MAPPING_BY_CASSANDRA_TABLE_ID_Q);) {
                pstmt.setInt(1, casssandraTblId);
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
                    "       m.mapped_col_name, " +
                    "       m.col_format, " +
                    "       m.column_size, " +
                    "       m.col_pattern, " +
                    "       m.nullable, " +
                    "       m.is_object" +
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

    public Map<String, Map<String, Object>> getMetaMappingTableColumnsBySystemId(int sysId) throws SQLException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(META_FETCH_TABLE_COLUMNS_MAPPED_CASSANDRA_COLUMNS_BY_SYSTEM_ID);) {
                pstmt.setInt(1, sysId);
                ResultSet rs = pstmt.executeQuery();

                Map<String, Map<String, Object>> tables = new HashMap<>();

                while (rs.next()) {
                    MetadataCassandraColumnBean mapping = BeanBuilders.buildMetaTableToCassandraColumnMapping(rs);

                    String tableName = rs.getString("table_name");
                    if (tableName == null) continue;
                    Map<String, Object> cols = tables.get(tableName);
                    if (cols == null) {
                        cols = new HashMap<>();
                        MetadataTableCassandraBean tableMetadata= BeanBuilders.buildCassandraMetadata(rs);
                        cols.put("tableMetadata", tableMetadata);
                    }
                    if(mapping != null) {
                        cols.put(rs.getString("col_name"), mapping);
                    }
                    tables.put(tableName, cols);
                }

                return tables;
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

    private static final String INSERT_INTO_PTABLE_COL_TO_CASS =
            "INSERT INTO product_table_column_to_cassandra(cass_table_id, col_id, created_by) VALUES (?, ?, ?)";
    private static final String INSERT_INTO_PTABLE_COL_TO_CASS_COL_Q =
            "INSERT INTO product_table_column_to_cassandra_column_mapping " +
            "(ptable_col_to_cass_id, mapped_col_name, column_size, col_format, col_pattern, nullable, is_object, description, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public void persist(String tableName, List<ProductTableColumnToCassandraColumn> tableToCassandraColumnMappings, String username) throws SQLException, MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            this.setAutocommit(conn, false);
            try (PreparedStatement pstmt0 = conn.prepareStatement(INSERT_INTO_PTABLE_COL_TO_CASS, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pstmt1 = conn.prepareStatement(INSERT_INTO_PTABLE_COL_TO_CASS_COL_Q);) {

                for (ProductTableColumnToCassandraColumn mapping: tableToCassandraColumnMappings) {
                    pstmt0.setInt(1, mapping.getCassandraTableId());
                    pstmt0.setInt(2, mapping.getColumnId());
                    pstmt0.setString(3, username);
                    pstmt0.executeUpdate();

                    ResultSet rs = pstmt0.getGeneratedKeys();
                    if (rs != null && rs.next()) {
                        int id = rs.getInt(1);
                        rs.close();

                        pstmt1.setInt(1, id);
                        pstmt1.setString(2, mapping.getMappedColumnName());
                        pstmt1.setInt(3, mapping.getColumnSize());
                        pstmt1.setString(4, mapping.getColumnFormat());
                        pstmt1.setString(5, mapping.getColumnPattern());
                        pstmt1.setString(6, mapping.isNullable()?"yes":"no");
                        pstmt1.setString(7, mapping.isTargetField()?"yes":"no");
                        pstmt1.setString(8, mapping.getDescription());
                        pstmt1.setString(9, username);
                        pstmt1.executeUpdate();
                    }
                    else {
                        logger.debug("Persisted to product_table_column_to_cassandra failed");
                    }
                }

/*
                these following code can be used for batch insertion
                for (MfgSystemTableToCassandraColumnMapping mapping: tableToCassandraColumnMappings) {
                    pstmt0.setInt(1, mapping.getCassandraTableId());
                    pstmt0.setInt(2, mapping.getColumnId());
                    pstmt0.setString(3, username);
                    pstmt0.addBatch();
                }

                int[] updateCounts = pstmt0.executeBatch();
                rs = pstmt0.getGeneratedKeys();
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

                for (MfgSystemTableToCassandraColumnMapping mapping: tableToCassandraColumnMappings) {
                    pstmt0.setInt(1, mapping.getCassandraTableId());
                    pstmt0.setInt(2, mapping.getColumnId());
                    pstmt0.setString(3, username);
                    pstmt0.addBatch();
                }
*/
                conn.commit();
            }
        }
        catch (SQLException e) {
            this.rollback(conn);
            throw new MfgException(String.format("Exception caught when saving product_table_column_to_cassandra_column_mapping '%s'", tableName), e);
        }
        finally {
            this.setAutocommit(conn, true);
            closeAllQuietly(null, conn, null);
        }
    }

    private static final String UPDATE_Q =
            "UPDATE product_table_column_to_cassandra_column_mapping " +
                    " SET mapped_col_name = ?, column_size = ?, col_format = ?, col_pattern = ?, nullable = ?, " +
                    " description = ?, last_updated = ?, updated_by = ?, is_object = ?" +
                    " WHERE id = ?";
    public void update(String tableName, ProductTableColumnToCassandraColumn cassandraColumnMapping, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                pstmt.setString(1, cassandraColumnMapping.getMappedColumnName());
                pstmt.setInt(2, cassandraColumnMapping.getColumnSize());
                pstmt.setString(3, cassandraColumnMapping.getColumnFormat());
                pstmt.setString(4, cassandraColumnMapping.getColumnPattern());
                pstmt.setString(5, cassandraColumnMapping.isNullable()?"yes":"no");
                pstmt.setString(6, cassandraColumnMapping.getDescription());
                pstmt.setTimestamp(7, new Timestamp(new java.util.Date().getTime()));
                pstmt.setString(8, username);
                pstmt.setString(9, cassandraColumnMapping.isTargetField() ? "yes":"no");
                pstmt.setInt(10, cassandraColumnMapping.getId());
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

    public void update(String tableName, List<ProductTableColumnToCassandraColumn> cassandraColumnMappings, String username) throws MfgException {
        Connection conn = null;

        try {
            conn = this.sqlDatasource.getConnection();
            this.setAutocommit(conn, false);
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_Q)) {
                for (ProductTableColumnToCassandraColumn mapping: cassandraColumnMappings) {
                    pstmt.setString(1, mapping.getMappedColumnName());
                    pstmt.setString(1, mapping.getMappedColumnName());
                    pstmt.setInt(2, mapping.getColumnSize());
                    pstmt.setString(3, mapping.getColumnFormat());
                    pstmt.setString(4, mapping.getColumnPattern());
                    pstmt.setString(5, mapping.isNullable()?"yes":"no");
                    pstmt.setString(6, mapping.getDescription());
                    pstmt.setTimestamp(7, new Timestamp(new java.util.Date().getTime()));
                    pstmt.setString(8, username);
                    pstmt.setString(9, mapping.isTargetField() ? "yes":"no");
                    pstmt.setInt(10, mapping.getId());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
            }
        }
        catch (SQLException e) {
            this.rollback(conn);
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when updating mfg_system_table_to_cassandra_column_mapping '%s'", tableName), e);
        }
        finally {
            this.setAutocommit(conn, true);
            closeAllQuietly(null, conn, null);
        }
    }

    private static final String DELETE_MAPPING_BY_CASSANDRA_TABLE_ID =
            "DELETE c FROM product_table_column_to_cassandra t " +
            " INNER JOIN product_table_column_to_cassandra_column_mapping c ON t.id = c.ptable_col_to_cass_id " +
            " WHERE t.cass_table_id = ";

    private static final String DELETE_JUNKCTION_BY_CASSANDRA_TABLE_ID =
            "DELETE t FROM product_table_column_to_cassandra t WHERE t.cass_table_id = ";
    public void deleteByCassandraTableId(int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            this.setAutocommit(conn, false);
            String q = DELETE_MAPPING_BY_CASSANDRA_TABLE_ID + id;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);

                q = DELETE_JUNKCTION_BY_CASSANDRA_TABLE_ID + id;
                stmt.executeUpdate(q);
                conn.commit();
            }
        }
        catch (SQLException e) {
            this.rollback(conn);
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting product_table_column_to_cassandra_column_mapping with cassandra table id '%s'", id), e);
        }
        finally {
            this.setAutocommit(conn, false);
            closeAllQuietly(null, conn, null);
        }
    }

    public void deleteByColumnId(int ptableColCassRefId, int id) throws MfgException {
        Connection conn = null;
        try {
            conn = this.sqlDatasource.getConnection();
            String q = "DELETE FROM product_table_column_to_cassandra_column_mapping WHERE id = " + id;
            this.setAutocommit(conn, false);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(q);

                q = "DELETE FROM product_table_column_to_cassandra WHERE id = " + ptableColCassRefId;
                stmt.executeUpdate(q);
                conn.commit();
            }
        }
        catch (SQLException e) {
            this.rollback(conn);
            e.printStackTrace();
            throw new MfgException(String.format("Exception caught when deleting product_table_column_to_cassandra_column_mapping with id '%s'", id), e);
        }
        finally {
            this.setAutocommit(conn, false);
            closeAllQuietly(null, conn, null);
        }
    }

    public static void main(String[] args) {
        MfgJDBCDatasourceConfiguration config = createTestConfig();
        try {
            SQLDatasource ds = new SQLDatasource(config);
            ds.connect();

            ProductTableColumnToCassandraColumnDAO dao = new ProductTableColumnToCassandraColumnDAO(ds);
            List<ProductTableColumnToCassandraColumn> entities = dao.fetchAll();
            System.err.println(entities);

            List<TableToCassandraColumnsMapping> list = dao.fetchAllCassandraColumnsByCassandraTableId(1);
            System.err.println(list);

            dao.deleteByColumnId(163, 161);
        } catch (SQLException | MfgException e) {
            e.printStackTrace();
        }
    }
}

