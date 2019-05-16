package com.huawei.imbp.rt.handler;


import com.datastax.driver.core.PagingState;
import com.google.common.base.Throwables;
import com.huawei.imbp.rt.common.InputParameter;
import com.huawei.imbp.rt.entity.AoiEntity;
import com.huawei.imbp.rt.entity.AoiKey;
import com.huawei.imbp.rt.entity.AoiKey;
import com.huawei.imbp.rt.repository.AoiRepository;
import com.huawei.imbp.rt.service.CassandraService;
import com.huawei.imbp.rt.util.DataUtil;
import com.huawei.imbp.rt.util.Logging;
import com.huawei.imbp.rt.util.ServiceUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
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
@Log4j2
public class RtServiceHandler {


    @Autowired
    public CassandraService cassandraService;


    public Mono<ServerResponse> retrieveDataSingle(ServerRequest serverRequest) {

        log.debug("retrieve single data");

        try {
            InputParameter input = ServiceUtil.getInputParam(serverRequest);
            return cassandraService.getOneData(input);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }

    }

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
        CassandraPageRequest pageable = null;
        try{
            InputParameter input = ServiceUtil.getInputParam(serverRequest);
            Optional<String> pageState = serverRequest.queryParam("pageState");

            //Pageable pageable = PageRequest.of(input.getPage(), input.getSize());
            if(pageState.isPresent()){
                PagingState pagingState = PagingState.fromString(pageState.get());
                pageable = CassandraPageRequest.of(PageRequest.of(input.getPage(), input.getSize()),pagingState);
            }else {
                pageable = CassandraPageRequest.of(input.getPage(), input.getSize());
            }
            return cassandraService.getAoiPageData(input, pageable);
        }catch (Exception e){
            log.error(Throwables.getStackTraceAsString(e));
            return ServerResponse.badRequest().syncBody(e.getMessage());
        }

    }



}

