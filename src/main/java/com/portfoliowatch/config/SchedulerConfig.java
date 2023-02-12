package com.portfoliowatch.config;

import com.portfoliowatch.service.PortfolioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private CacheManager cacheManager;

    @Scheduled(cron = "0 0 0 ? * MON-SUN", zone="GMT+5.00")
    public void nightlyJobs() {
        for(String name : cacheManager.getCacheNames()){
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                log.info("Clearing cache for: {}", name);
                cache.clear();
            }
        }
    }
}
