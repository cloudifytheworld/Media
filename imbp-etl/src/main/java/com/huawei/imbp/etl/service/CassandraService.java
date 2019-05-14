package com.huawei.imbp.etl.service;

import com.huawei.imbp.etl.repository.AoiRepository;
import com.huawei.imbp.etl.entity.AoiEntity;
import com.huawei.imbp.etl.transform.ConversionData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Charles(Li) Cai
 * @date 2/15/2019
 */

@Component
@RefreshScope
@Log4j2
public class CassandraService {

    @Value("${request.timeout}")
    public int timeout;

    @Autowired
    public LoggingService loggingService;

    @Autowired
    private AoiRepository aoiRepository;

    public Mono<ServerResponse> onProcess(final Map requestData) {

        log.debug("enter CassandraService handling process");

        try {
            Map<String, Object> payload = (Map)requestData.get("payload");
            AoiEntity entity = ConversionData.convert(payload);
            aoiRepository.insert(entity).subscribe();
            return Mono.empty();
        }catch (Exception e){
            loggingService.onFailure(e, requestData);
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }



    }

 }
