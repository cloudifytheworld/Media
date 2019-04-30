package com.huawei.mfg.util;

import com.google.common.base.Strings;
import com.huawei.mfg.bean.*;
import com.huawei.mfg.bean.meta.MetadataCassandraColumnBean;
import com.huawei.mfg.bean.meta.MetadataColumnBean;
import com.huawei.mfg.bean.meta.MetadataHBaseColumnBean;
import com.huawei.mfg.bean.meta.MetadataTableCassandraBean;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("Duplicates")
final public class BeanBuilders {

    public static <R extends BaseBean> void buildBaseBean(ResultSet rs, R bean) throws SQLException {
        if (rs == null) return ;

        bean.setId(rs.getInt("id"));
        bean.setCreatedBy(rs.getString("created_by"));
        bean.setUpdatedBy(rs.getString("updated_by"));
        bean.setCreated(rs.getTimestamp("created"));
        bean.setLastUpdated(rs.getTimestamp("last_updated"));
    }

    public static MfgSystem buildMfgSystem(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystem entity = new MfgSystem();
        buildBaseBean(rs, entity);
        entity.setName(rs.getString("name"));

        return entity;
    }

    public static MfgSystemTable buildMfgSystemTable(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTable entity = new MfgSystemTable();
        buildBaseBean(rs, entity);
        entity.setSystemId(rs.getInt("system_id"));
        entity.setTableName(rs.getString("table_name"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));
        entity.setSource(rs.getString("source"));
        entity.setPrimaryKeys(rs.getString("primary_keys"));
        entity.setUniqueKeys(rs.getString("unique_keys"));
        entity.setSampleTableName(rs.getString("sample_table_name"));

        return entity;
    }

    public static void buildMfgSystemTable(MfgSystemTable entity, ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return;

        buildBaseBean(rs, entity);
        entity.setSystemId(rs.getInt("system_id"));
        entity.setTableName(rs.getString("table_name"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));
        entity.setSource(rs.getString("source"));
        entity.setPrimaryKeys(rs.getString("primary_keys"));
        entity.setUniqueKeys(rs.getString("unique_keys"));
        entity.setSampleTableName(rs.getString("sample_table_name"));
    }

    public static MfgSystemTableColumns buildMfgSystemTableColumns(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableColumns entity = new MfgSystemTableColumns();
        buildBaseBean(rs, entity);
        entity.setSystemId(rs.getInt("system_id"));
        entity.setTableName(rs.getString("table_name"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));
        entity.setSource(rs.getString("source"));
        entity.setPartitionKeys(rs.getString("partition_keys"));
        entity.setPrimaryKeys(rs.getString("primary_keys"));
        entity.setUniqueKeys(rs.getString("unique_keys"));
        entity.setSampleTableName(rs.getString("sample_table_name"));

        return entity;
    }

    public static MfgSystemTableTopic buildMfgSystemTableTopic(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableTopic entity = new MfgSystemTableTopic();
        buildBaseBean(rs, entity);
        entity.setTableId(rs.getInt("table_id"));
        entity.setTopic(rs.getString("topic"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));

        return entity;
    }

    public static MfgSystemTableToHbase buildMfgSystemTableToHbase(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableToHbase entity = new MfgSystemTableToHbase();
        buildBaseBean(rs, entity);
        entity.setTableId(rs.getInt("table_id"));
        entity.setTableName(rs.getString("table_name"));
        entity.setColumnFamilies(rs.getString("column_families"));
        entity.setRowKeyColumns(rs.getString("row_key_columns"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));

        return entity;
    }

    public static MfgSystemTableToCassandra buildMfgSystemTableToCassandra(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableToCassandra entity = new MfgSystemTableToCassandra();
        buildBaseBean(rs, entity);
        entity.setTableId(rs.getInt("table_id"));
        entity.setTableName(rs.getString("table_name"));
        entity.setKeySpace(rs.getString("key_space"));
        entity.setPartitionKeys(rs.getString("partition_keys"));
        entity.setClusteringKeys(rs.getString("clustering_keys"));
        entity.setDescription(rs.getString("description"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));

        return entity;
    }

    public static void buildMfgSystemTableToCassandra(MfgSystemTableToCassandra entity, ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return;

        buildBaseBean(rs, entity);
        entity.setTableId(rs.getInt("table_id"));
        entity.setTableName(rs.getString("table_name"));
        entity.setKeySpace(rs.getString("key_space"));
        entity.setPartitionKeys(rs.getString("partition_keys"));
        entity.setClusteringKeys(rs.getString("clustering_keys"));
        entity.setDescription(rs.getString("description"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));
    }

    public static MfgSystemTableToHive buildMfgSystemTableToHive(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableToHive entity = new MfgSystemTableToHive();
        buildBaseBean(rs, entity);
        entity.setTableId(rs.getInt("table_id"));
        entity.setTableName(rs.getString("table_name"));
        entity.setLocation(rs.getString("location"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));

        return entity;
    }

    public static MfgSystemTableMapping buildMfgSystemTableMapping(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableMapping entity = new MfgSystemTableMapping();
        buildBaseBean(rs, entity);
        entity.setTableId(rs.getInt("table_id"));
        entity.setTableName(rs.getString("table_name"));
        entity.setForDb(rs.getString("for_db"));

        return entity;
    }

    public static MfgSystemTableColumn buildMfgSystemTableColumn(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableColumn entity = null;
        int cid = rs.getInt("cid");
        if (cid > 0) {
            entity = new MfgSystemTableColumn();
            buildBaseBean(rs, entity);
            entity.setTableId(rs.getInt("table_id"));
            entity.setColumnName(rs.getString("col_name"));
            entity.setColumnDataType(rs.getString("col_data_type"));
            entity.setColumnSize(rs.getInt("column_size"));

            String nullable = rs.getString("nullable");
            if (!Strings.isNullOrEmpty(nullable)) {
                entity.setNullable(nullable.equals("yes"));
            }
            entity.setColumnFormat(rs.getString("col_format"));
            entity.setColumnPattern(rs.getString("col_pattern"));
        }
        return entity;
    }

    public static MfgSystemTableColumnMapping buildMfgSystemTableColumnMapping(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableColumnMapping entity = new MfgSystemTableColumnMapping();
        buildBaseBean(rs, entity);
        entity.setColumnId(rs.getInt("col_id"));
        entity.setShortColumnName(rs.getString("short_col_name"));
        entity.setForDb(rs.getString("for_db"));
        entity.setColumnDataType(rs.getString("col_data_type"));
        entity.setColumnFamily(rs.getString("column_family"));

        return entity;
    }

    public static MfgSystemTableToHbaseColumnMapping buildMfgSystemTableToHbaseColumnMapping(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableToHbaseColumnMapping entity = new MfgSystemTableToHbaseColumnMapping();
        buildBaseBean(rs, entity);
        entity.setColumnId(rs.getInt("col_id"));
        entity.setMappedColumnName(rs.getString("mapped_col_name"));
        entity.setColumnFamily(rs.getString("column_family"));

        return entity;
    }

    public static MfgSystemTableToCassandraColumnMapping buildMfgSystemTableToCassandraColumnMapping(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTableToCassandraColumnMapping entity = new MfgSystemTableToCassandraColumnMapping();
        buildBaseBean(rs, entity);
        entity.setColumnId(rs.getInt("col_id"));
        entity.setMappedColumnName(rs.getString("mapped_col_name"));
        entity.setCassandraTableId(rs.getInt("cass_table_id"));

        return entity;
    }

    public static ProductTableColumnToCassandraColumn buildProductTabblColumnToCassandraColumn(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        ProductTableColumnToCassandraColumn entity = new ProductTableColumnToCassandraColumn();
        buildBaseBean(rs, entity);
        entity.setPtableColumnToCassandraId(rs.getInt("ptable_col_to_cass_id"));
        entity.setMappedColumnName(rs.getString("mapped_col_name"));
//        entity.setCassandraTableId();
//        entity.setColumnId();
        entity.setColumnCassandraRefId(rs.getInt("ptable_col_to_cass_id"));
        entity.setColumnFormat(rs.getString("col_format"));
        entity.setColumnPattern(rs.getString("col_pattern"));
        entity.setColumnSize(rs.getInt("column_size"));
        entity.setNullable(rs.getString("nullable").equals("yes"));
        entity.setDescription(rs.getString("description"));

        return entity;
    }

    @SuppressWarnings("Duplicates")
    public static TableColumnsAndMapping buildTableColumnsAndMapping(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        TableColumnsAndMapping entity = new TableColumnsAndMapping();
        entity.setTableName(rs.getString("table_name"));
        entity.setColumnName(rs.getString("col_name"));
        entity.setColumnDataType(rs.getString("col_data_type"));
        entity.setShortColumnName(rs.getString("short_col_name"));
        entity.setColumnFamily(rs.getString("column_family"));
        entity.setMappedDataType(rs.getString("mapped_data_type"));
        entity.setForDb(rs.getString("for_db"));

        return entity;
    }

    public static TableToHbaseColumnsMapping buildTableColumnsHbaseMapping(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        TableToHbaseColumnsMapping entity = new TableToHbaseColumnsMapping();
        entity.setTableName(rs.getString("table_name"));
        entity.setHbaseTableName(rs.getString("hbase_table_name"));
        entity.setColumnName(rs.getString("col_name"));
        entity.setColumnDataType(rs.getString("col_data_type"));
        entity.setMappedColumnName(rs.getString("mapped_col_name"));
        entity.setColumnFamily(rs.getString("column_family"));

        return entity;
    }

    public static TableToCassandraColumnsMapping buildTableToCassandraColumnsMapping(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        TableToCassandraColumnsMapping entity = new TableToCassandraColumnsMapping();
        entity.setTableName(rs.getString("table_name"));
        entity.setCassandraTableName(rs.getString("cass_table_name"));
        entity.setColumnName(rs.getString("col_name"));
        entity.setColumnDataType(rs.getString("col_data_type"));
        entity.setMappedColumnName(rs.getString("mapped_col_name"));
//        entity.setDescription(rs.getString("description"));
        entity.setColumnId(rs.getInt("col_id"));
        entity.setCassandraColumnId(rs.getInt("cass_col_id"));
        entity.setCassandraTableId(rs.getInt("cass_table_id"));
        entity.setColumnCassandraRefId(rs.getInt("col_cassandra_ref_id"));
        entity.setKeyspace(rs.getString("key_space"));
        entity.setPartitionKeys(rs.getString("partition_keys"));
        entity.setClusteringKeys(rs.getString("clustering_keys"));
        entity.setColumnFormat(rs.getString("col_format"));
        entity.setColumnPattern(rs.getString("col_pattern"));
        entity.setNullable(rs.getString("nullable").equals("yes"));
        entity.setObject(rs.getString("is_object").equals("yes"));
        entity.setColumnSize(rs.getInt("column_size"));

        return entity;
    }

    public static MetadataCassandraColumnBean buildMetaTableToCassandraColumnMapping(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MetadataCassandraColumnBean entity = new MetadataCassandraColumnBean();
        String columnName = rs.getString("mapped_col_name");
        if (StringUtils.isEmpty(columnName)) return null;

        entity.setColumnDataType(rs.getString("col_data_type"));
        entity.setMappedColumnName(columnName);
        entity.setColumnFormat(rs.getString("col_format"));
        entity.setColumnPattern(rs.getString("col_pattern"));
        entity.setNullable(rs.getString("nullable").equals("yes"));
        entity.setTargetField(rs.getString("is_object").equals("yes"));
        entity.setColumnSize(rs.getInt("column_size"));

        return entity;
    }

    public static MetadataTableCassandraBean buildCassandraMetadata(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MetadataTableCassandraBean entity = new MetadataTableCassandraBean();
        entity.setKeyspace(rs.getString("key_space"));
        entity.setTableName(rs.getString("ctable_name"));
        entity.setPrimaryKeys(rs.getString("partition_keys"));
        String clusteringKeys = rs.getString("clustering_keys");
        if(!StringUtils.isEmpty(clusteringKeys)){
            entity.setPrimaryKeys(entity.getPrimaryKeys()+","+clusteringKeys);
        }

        return entity;
    }

    public static MetadataColumnBean buildMetaTableToHbaseColumnsMapping(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MetadataHBaseColumnBean entity = new MetadataHBaseColumnBean();
        entity.setColumnDataType(rs.getString("col_data_type"));
        entity.setColumnName(rs.getString("mapped_col_name"));
        entity.setColumnFamily(rs.getString("column_family"));
        entity.setTableName(rs.getString("hbase_table_name"));
        return entity;
    }

    public static Pair<MfgSystemTable, MfgSystemTableTopic> buildTableTopicPair(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTable table = new MfgSystemTable();
        MfgSystemTableTopic topic = new MfgSystemTableTopic();

        table.setId(rs.getInt("tid"));
        table.setTableName(rs.getString("table_name"));

        topic.setId(rs.getInt("topic_id"));
        topic.setTopic(rs.getString("topic"));

        Pair<MfgSystemTable, MfgSystemTableTopic> pair = Pair.of(table, topic);

        return pair;
    }

    public static MfgSystemTablePkOffset buildMfgSystemTablePkOffset(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgSystemTablePkOffset entity = new MfgSystemTablePkOffset();
        buildBaseBean(rs, entity);
        entity.setTableId(rs.getInt("table_id"));
        entity.setLastOffset(rs.getString("last_offset"));

        return entity;
    }

    public static MfgTableColumnMapping buildMfgTableColumnMapping(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgTableColumnMapping entity = new MfgTableColumnMapping();
        buildBaseBean(rs, entity);
        entity.setColumnName(rs.getString("column_name"));
        entity.setAbbrColumnName(rs.getString("abbr_column_name"));
        entity.setConciseAbbrColumnName(rs.getString("concise_abbr_column_name"));
        entity.setDescription(rs.getString("description"));

        return entity;
    }

    public static MfgCassandraConfiguration buildMfgCassandraConfiguration(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgCassandraConfiguration entity = new MfgCassandraConfiguration();
        buildBaseBean(rs, entity);
        entity.setHosts(rs.getString("hosts"));
        entity.setPort(rs.getInt("port"));
        entity.setUsername(rs.getString("username"));
        entity.setPassword(rs.getString("password"));
        entity.setSslEnabled(rs.getString("ssl_enabled").equals("yes"));
        entity.setDescription(rs.getString("description"));

        return entity;
    }

    public static MfgMetadataDatabaseConfiguration buildMfgMetadataDatabaseConfiguration(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgMetadataDatabaseConfiguration entity = new MfgMetadataDatabaseConfiguration();
        buildBaseBean(rs, entity);
        entity.setHost(rs.getString("host"));
        entity.setPort(rs.getInt("port"));
        entity.setUsername(rs.getString("username"));
        entity.setPassword(rs.getString("password"));
        entity.setDatabaseType(rs.getString("db_type"));
        entity.setDatabase(rs.getString("db_name"));
        entity.setConnectionPoolName(rs.getString("connection_pool_name"));
        entity.setTestQuery(rs.getString("test_query"));
        entity.setDescription(rs.getString("description"));

        return entity;
    }

    public static MfgDiscoveryServiceConfiguration buildMfgDiscoveryServiceConfiguration(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgDiscoveryServiceConfiguration entity = new MfgDiscoveryServiceConfiguration();
        buildBaseBean(rs, entity);
        entity.setHost(rs.getString("host"));
        entity.setPort(rs.getInt("port"));
        entity.setDescription(rs.getString("description"));

        return entity;
    }

    public static MfgEtlServiceKey buildMfgEtlServiceKey(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgEtlServiceKey entity = new MfgEtlServiceKey();
        buildBaseBean(rs, entity);
        entity.setServiceKey(rs.getString("service_key"));
        entity.setSource(rs.getString("source"));
        entity.setEnabled(rs.getString("enabled").equals("yes"));
        entity.setDescription(rs.getString("description"));

        return entity;
    }

    public static MfgEtlServiceConfiguration buildMfgEtlServiceConfiguration(ResultSet rs) throws SQLException {
        if (rs == null && !rs.next()) return null;

        MfgEtlServiceConfiguration entity = new MfgEtlServiceConfiguration();
        buildBaseBean(rs, entity);
        entity.setKeyId(rs.getInt("key_id"));
        entity.setServiceValue(rs.getString("service_value"));
        entity.setDescription(rs.getString("description"));

        return entity;
    }

}
