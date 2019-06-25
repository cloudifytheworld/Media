package com.fw.imbp.etl;

import ch.qos.logback.classic.Level;
import com.fw.imbp.etl.util.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Charles(Li) Cai
 * @date 2/15/2019
 */

@SpringBootApplication(exclude= CassandraDataAutoConfiguration.class)
@EnableDiscoveryClient
@RefreshScope
@Configuration
public class ImbpEtlApplication {

    @Value("${logging.level-root}")
    public Level logLevel;

    @Bean
    @RefreshScope
    public Logging log(){
        Logging logging = new Logging();
        logging.setLogLevel(logLevel);
        return logging;
    }

    public static void main(String[] args) {
        SpringApplication.run(ImbpEtlApplication.class, args);
    }

}

