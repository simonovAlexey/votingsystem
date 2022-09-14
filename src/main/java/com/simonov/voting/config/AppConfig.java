package com.simonov.voting.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@EnableCaching
public class AppConfig {

    @Profile("!test")
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server(@Value("${app.h2-server.port:9099}") String port) throws SQLException {
        log.info("Try to initialize H2 TCP server at port: {}", port);
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", port);
    }

    @Bean
    public Module module() {
        return new Hibernate5Module();
    }


    @Bean
    public CacheManager dbCacheManager(@Value("${app.cache_ttl_min:1}") Integer ttlMin) {
        log.info("Start cache for app with ttl {} min", ttlMin);
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(ttlMin, TimeUnit.MINUTES)
                .recordStats());

        return cacheManager;
    }
}