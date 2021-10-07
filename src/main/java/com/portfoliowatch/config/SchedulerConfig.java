package com.portfoliowatch.config;

import com.portfoliowatch.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TransactionService transactionService;

    private final List<String> every30MinuteCache;

    public SchedulerConfig() {
        every30MinuteCache = new ArrayList<>();
        every30MinuteCache.add("quote-list");
    }

    @Scheduled(cron = "0 0 0 ? * MON-SUN", zone="GMT+5.00")
    public void nightlyJobs() {
        transactionService.generateAccountLotListMap();
        for(String name : cacheManager.getCacheNames()){
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    @Scheduled(cron = "0 0/15 * * * ?", zone="GMT+5.00")
    public void every30MinutesJob() {
        for(String name : every30MinuteCache){
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    @Scheduled(cron = "0 0 12 ? * MON-SUN", zone="GMT+5.00")
    public void noonJobs() {
        transactionService.generateAccountLotListMap();
    }

}
