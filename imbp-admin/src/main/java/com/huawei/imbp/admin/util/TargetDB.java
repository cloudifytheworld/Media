package com.huawei.imbp.admin.util;

import com.huawei.mfg.bean.*;
import com.huawei.mfg.dao.MfgSystemTableDAO;
import com.huawei.mfg.dao.MfgSystemTableToCassandraColumnMappingDAO;
import com.huawei.mfg.dao.MfgSystemTableToHbaseColumnMappingDAO;
import com.huawei.mfg.dao.MfgSystemTableToTopicDAO;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;


/**
 * @author Charles(Li) Cai
 * @date 2/25/2019
 */

@Slf4j
public enum TargetDB {

//    HBASE(Constant.HBASE){
//
//        @Override
//        public <T extends Map> void execute(SQLDatasource datasource, int systemId, T metadata,
//                                            Map<String, Map<String, Object>> tableColumns) {
//
//            try {
//                MfgSystemTableToHbaseColumnMappingDAO systemTableColumnHbaseMappingDAO =
//                        new MfgSystemTableToHbaseColumnMappingDAO(datasource);
//                Map<String, Map<String, MetadataColumnBean>> hbaseMapping =
//                        systemTableColumnHbaseMappingDAO.getMetaMappingTableColumnColumnFamilyMappingBySystem(systemName);
//                GenerateMetadata.handleDB(this.name().toLowerCase(), systemName, hbaseMapping, metadata, tableColumns);
//
//            }catch (Exception e){
//                log.error(this.name()+" target metadata "+e.getMessage());
//            }
//        }
//    },

    CASSANDRA(Constant.CASSANDRA){
        @Override
        public <T extends Map> void execute(SQLDatasource datasource, MfgSystem mfgSystem, T metadata,
                                            Map<String, Map<String, Object>> tableColumns) {

            try {
//                MfgSystemTableToCassandraColumnMappingDAO systemTableColumnCassandraMappingDAO =
//                        new MfgSystemTableToCassandraColumnMappingDAO(datasource);
//                Map<String, Map<String, Object>> cassandraMapping =
//                        systemTableColumnCassandraMappingDAO.getMetaMappingTableColumnsBySystemId(mfgSystem.getId());
//                GenerateMetadata.handleDB(this.name().toLowerCase(), mfgSystem, cassandraMapping, metadata, tableColumns);

            }catch(Exception e){
                log.error(this.name()+" target metadata "+e.getMessage());
            }
        }
    },
    COLUMNS(Constant.COLUMNS){
        @Override
        public <T extends Map> void execute(SQLDatasource datasource, MfgSystem mfgSystem, T metadata,
                                            Map<String, Map<String, Object>> tableColumns) {

            try {
                if(tableColumns == null){
                    tableColumns = getTableColumns(datasource, mfgSystem);
                }
                GenerateMetadata.handleColumns(this.name().toLowerCase(), mfgSystem, tableColumns, metadata);
            }catch(Exception e){
                log.error(this.name()+" target metadata "+e.getMessage());
            }
        }
    },
    TOPIC(Constant.TOPIC){
        @Override
        public <T extends Map> void execute(SQLDatasource datasource, MfgSystem mfgSystem, T metadata,
                                            Map<String, Map<String, Object>> tableColumns ) {
            //mapping to system id is not implemented
            try {
//                MfgSystemTableToTopicDAO dao = new MfgSystemTableToTopicDAO(datasource);
//                List<Pair<MfgSystemTable, MfgSystemTableTopic>> topicMapping =
//                        dao.fetchAllTableTopicMappingBySystemName(systemName);
//                GenerateMetadata.handleTopic(this.name().toLowerCase(), systemName, topicMapping, metadata);

            }catch (Exception e){
                log.error(this.name()+" target metadata "+e.getMessage());

            }
        }
    },

    HIVE(Constant.HIVE){
        @Override
        public <T extends Map> void execute(SQLDatasource datasource, MfgSystem mfgSystem, T metadata,
                                            Map<String, Map<String, Object>> tableColumns) {

        }
    };

    private String dataBaseType;

    TargetDB(String dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public static TargetDB getName(String dataBaseType){
        try{
            return valueOf(dataBaseType);
        }catch (Exception e){
            return null;
        }
    }

    public static Map<String, Map<String, Object>> getTableColumns(SQLDatasource datasource, MfgSystem mfgSystem){

        try {
//            MfgSystemTableDAO systemTableDAO = new MfgSystemTableDAO(datasource);
//            Map<String, Map<String, Object>> tableColumns = systemTableDAO.fetchAllTableMetaColumnsBySystemId(mfgSystem.getId());
//            return tableColumns;
        }catch (Exception e){
            log.error("target metadata "+e.getMessage());
        }
        return null;
    }

    public abstract <T extends Map> void execute(SQLDatasource datasource, MfgSystem mfgSystem, T metadata,
                                                 Map<String, Map<String, Object>> tableColumns );
}
