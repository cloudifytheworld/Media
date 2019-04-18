package com.huwei.imbp.admin.service;

import com.google.gson.Gson;
import com.huawei.mfg.bean.BaseMappingBean;
import com.huawei.mfg.bean.MfgSystem;
import com.huawei.mfg.dao.MfgSystemDAO;
import com.huawei.mfg.pool.SQLDatasource;
import com.huawei.mfg.util.Constants;
import com.huwei.imbp.admin.rest.ServiceCallManager;
import com.huwei.imbp.admin.util.TargetDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MetaDataService {


    @Autowired
    public ServiceCallManager serviceCallManager;

    @Autowired
    public DataSourceService dataSourceService;

    public Mono<ServerResponse> handleMetadata(ServerRequest serverRequest){

        Map<String, Map<String, Map<String, Map<String, BaseMappingBean>>>> metadata = new HashMap<>();
        Map<String, String> tableToTopic = new HashMap<>();

        SQLDatasource datasource = dataSourceService.dataSource();
        MfgSystemDAO systemDao = new MfgSystemDAO(datasource);

        try{
            List<MfgSystem> mfgSystems =  systemDao.fetchAll();
            mfgSystems.stream().forEach( s -> {
                Arrays.stream(Constants.Target.values()).forEach(t ->{
                    TargetDB type = TargetDB.getName(t.name());
                    if(type != null) {
                        type.execute(datasource, s.getName(), metadata);
                    }
                });

            });
            Gson gson = new Gson();
            String strMetaData = gson.toJson(metadata);

            return ServerResponse.ok()
                    .body(serviceCallManager.pushToConsul(strMetaData), String.class);
        }catch (Exception e){
            log.error(e.getMessage());
            return ServerResponse.badRequest().body(Mono.just(e.getMessage()), String.class);
        }
    }
}
