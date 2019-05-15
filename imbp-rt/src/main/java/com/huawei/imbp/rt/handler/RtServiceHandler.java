package com.huawei.imbp.rt.handler;


import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.entity.AoiKey;
import com.huawei.imbp.rt.entity.AoiKey;
import com.huawei.imbp.rt.repository.AoiRepository;
import com.huawei.imbp.rt.service.CassandraService;
import com.huawei.imbp.rt.util.DataUtil;
import com.huawei.imbp.rt.util.Logging;
import com.huawei.imbp.rt.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static com.huawei.imbp.rt.common.Constant.IMAGE_PAGE_SIZE;
import static com.huawei.imbp.rt.common.Constant.AOI;
import java.util.Optional;


/**
 * @author Charles(Li) Cai
 * @date 04/09/2019
 */

@Component
public class RtServiceHandler {


    @Autowired
    public CassandraService cassandraService;

    @Autowired
    public Logging log;



    public Mono<ServerResponse> retrieveDataByDate(ServerRequest serverRequest) {

        log.debug("retrieve data service");

        Optional<String> system = serverRequest.queryParam("system");
        if(!system.isPresent()){
            return ServerResponse.badRequest().syncBody("must specify which system to retrieve");
        }

        Optional<String> from = serverRequest.queryParam("from");
        cassandraService.getDataByDate(system.get(), from.get());


        return Mono.empty();
    }


    public Mono<ServerResponse> retrieveDataByPagination(ServerRequest serverRequest){

        log.debug("retrieve data by pagination");
        Optional<String> pageStr = serverRequest.queryParam("page");
        Optional<String> sizeStr = serverRequest.queryParam("size");

        Integer page = DataUtil.checkValidInteger(pageStr.get());
        if(page == null){
            return ServerResponse.badRequest().syncBody("page either is missing or not number");
        }
        Integer size = DataUtil.checkValidInteger(sizeStr.get());
        Pageable pageable = PageRequest.of(page, size !=null ?size: IMAGE_PAGE_SIZE);

        Optional<String> system = serverRequest.queryParam("system");
        if(!system.isPresent()){
            return ServerResponse.badRequest().syncBody("must specify which system to retrieve");
        }

        switch (system.get()){
            case AOI:
                AoiKey aoiKey = ServiceUtil.getAoiKey(serverRequest);
                cassandraService.getAoiPageData(aoiKey, pageable);
                break;
            default:
                return ServerResponse.badRequest().syncBody(system.get()+ " system is not supported yet");
        }

        return Mono.empty();
    }



}

