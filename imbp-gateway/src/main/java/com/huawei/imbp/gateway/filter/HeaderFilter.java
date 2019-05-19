package com.huawei.imbp.gateway.filter;

import com.google.common.base.Throwables;
import com.huawei.imbp.gateway.util.Logging;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


/**
 * @author Charles(Li) Cai
 * @date 3/12/2019
 */


//@Component
//@RefreshScope
//public class HeaderFilter implements WebFilter {
//
//    @Value("${filter.imageHeader}")
//    public String imageHeaderName;
//
//    @Value("${filter.largeImagePath}")
//    public String largeImagePath;
//
//    @Value("${filter.imageSize}")
//    public Long largeImageSize;
//
//    @Autowired
//    public Logging log;


//    @Override
//    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
//
//        String imageSize = serverWebExchange.getRequest().getHeaders().getFirst(imageHeaderName);
//        if(!StringUtils.isEmpty(imageSize)){
//            try {
//                    long size = Long.parseLong(imageSize);
//                    if(size >= largeImageSize){
//                        log.debug(serverWebExchange.getRequest().getPath().toString());
//                        return webFilterChain.filter(serverWebExchange.mutate().request(serverWebExchange.getRequest().mutate()
//                               .path(largeImagePath).build()).build());
//                    }
//            }catch (Exception e){
//                log.error(Throwables.getStackTraceAsString(e));
//            }
//        }
//        return webFilterChain.filter(serverWebExchange);
//    }
//}
