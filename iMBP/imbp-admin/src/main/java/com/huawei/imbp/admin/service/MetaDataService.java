package com.huawei.imbp.admin.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huawei.imbp.admin.rest.ServiceCallManager;
import com.huawei.imbp.admin.util.TargetDB;
import com.huawei.mfg.bean.MfgSystem;
import com.huawei.mfg.dao.MfgSystemDAO;
import com.huawei.mfg.pool.SQLDatasource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 2/25/2019
 */

@Component
@Slf4j
@RefreshScope
public class MetaDataService {

    @Value("${db.target}")
    public List<String> target;


    @Autowired
    public ServiceCallManager serviceCallManager;

    @Autowired
    public DataSourceService dataSourceService;

    public Mono<ServerResponse> handleMetadataDB(ServerRequest serverRequest){

        Map<String, Map<String, Map<String, Map<String, Object>>>> metadata = new HashMap<>();

        SQLDatasource datasource = dataSourceService.dataSource();
        MfgSystemDAO systemDao = new MfgSystemDAO(datasource);

        try{
            List<MfgSystem> mfgSystems =  systemDao.fetchAll();
            mfgSystems.stream().forEach( s -> {

                final Map<String, Map<String, Object>> tableColumns =
                    target.contains(TargetDB.COLUMNS.name())?null:
                            TargetDB.getTableColumns(datasource, s);

                target.stream().forEach(n ->{
                    TargetDB type = TargetDB.getName(n);
                    if(type != null) {
                        type.execute(datasource, s, metadata, tableColumns);
                    }
                });
            });

            return handleResult(metadata);

        }catch (Exception e){
            log.error(e.getMessage());
            return ServerResponse.badRequest().body(Mono.just(e.getMessage()), String.class);
        }
    }


    public Mono<ServerResponse> handleMetadataTopic(ServerRequest serverRequest) {

        try{
            return ServerResponse.ok().body(Mono.just("unless a separate topic mapping, this feature is not handled yet"), String.class);

        }catch (Exception e){
            log.error(e.getMessage());
            return ServerResponse.badRequest().body(Mono.just(e.getMessage()), String.class);
        }
    }

    public Mono<ServerResponse> handleMetadataHive(ServerRequest serverRequest) {
        return ServerResponse.ok().body(Mono.just("not handle yet"), String.class);
    }

    private Mono<ServerResponse> handleResult(Map metadata){

        Gson gson = new GsonBuilder().serializeNulls().create();
        String strMetaData = gson.toJson(metadata);

        return strMetaData.length() != 0?
                ServerResponse.ok()
                        .body(serviceCallManager.pushToConsul(strMetaData), String.class)
                : ServerResponse.ok().body(Mono.just("Empty metadata generated"), String.class);
    }
}
