package com.huawei.imbp.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * @author Charles(Li) Cai
 * @date 2/25/2019
 */

@SpringBootApplication
@EnableDiscoveryClient
public class ImbpAdminApplication {


    @Bean
    public WebClient.Builder webClient() {
        return WebClient.builder();
    }

    public static void main(String[] args) {
        SpringApplication.run(ImbpAdminApplication.class, args);
    }

}
