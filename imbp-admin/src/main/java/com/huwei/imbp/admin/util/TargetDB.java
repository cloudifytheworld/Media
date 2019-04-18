package com.huwei.imbp.admin.util;

import com.huawei.mfg.bean.MfgSystem;
import com.huawei.mfg.bean.TableColumnsCassandraMapping;
import com.huawei.mfg.bean.TableColumnsHbaseMapping;
import com.huawei.mfg.dao.MfgSystemTableColumnCassandraMappingDAO;
import com.huawei.mfg.dao.MfgSystemTableColumnHbaseMappingDAO;
import com.huawei.mfg.pool.SQLDatasource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public enum TargetDB {

    HBASE("hbase"){

        @Override
        public <T extends Map> void execute(SQLDatasource datasource, String systemName, T metadata) {

            try {
                MfgSystemTableColumnHbaseMappingDAO systemTableColumnHbaseMappingDAO =
                        new MfgSystemTableColumnHbaseMappingDAO(datasource);
                Map<String, List<TableColumnsHbaseMapping>> hbaseMapping =
                        systemTableColumnHbaseMappingDAO.getTableColumnColumnFamilyMappingBySystem(systemName);
                GenerateMetadata.handleDB(this.name().toLowerCase(), systemName, hbaseMapping, metadata);
            }catch (Exception e){
                log.error(this.name()+" target metadata "+e.getMessage());
            }
        }
    },

    CASSANDRA("cassandra"){
        @Override
        public <T extends Map> void execute(SQLDatasource datasource, String systemName, T metadata) {

            try {
                MfgSystemTableColumnCassandraMappingDAO systemTableColumnCassandraMappingDAO =
                        new MfgSystemTableColumnCassandraMappingDAO(datasource);
                Map<String, List<TableColumnsCassandraMapping>> cassandraMapping =
                        systemTableColumnCassandraMappingDAO.getTableColumnMappingBySystem(systemName);
                GenerateMetadata.handleDB(this.name().toLowerCase(), systemName, cassandraMapping, metadata);
            }catch(Exception e){
                log.error(this.name()+" target metadata "+e.getMessage());
            }
        }
    },
    TOPIC("topic"){
        @Override
        public <T extends Map> void execute(SQLDatasource datasource, String systemName, T metadata) {

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
    public abstract <T extends Map> void execute(SQLDatasource datasource, String systemName, T metadata);
}
