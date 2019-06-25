package com.fw.imbp.gateway.filter;


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
//                log.error(e);
//            }
//        }
//        return webFilterChain.filter(serverWebExchange);
//    }
//}
