package com.fw.imbp.gateway;


import ch.qos.logback.classic.Level;
import com.fw.imbp.gateway.util.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
@Configuration
public class ImbpGatewayApplication {

    @Value("${logging.level-root}")
    public Level logLevel;

    @Bean
    @RefreshScope
    public Logging logger(){
        Logging logging = new Logging();
        logging.setLogLevel(logLevel);
        return logging;
    }

    @LoadBalanced
    @Bean
    public WebClient.Builder webClient() {
        return WebClient.builder();
    }

    public static void main(String[] args) {
        SpringApplication.run(ImbpGatewayApplication.class, args);
    }

}

