package com.portfoliowatch.config;

import com.portfoliowatch.service.EmailService;
import com.portfoliowatch.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TransactionService transactionService;

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

    @Scheduled(cron = "0 0 12 ? * MON-SUN", zone="GMT+5.00")
    public void noonJobs() {
        transactionService.generateAccountLotListMap();
    }

}
