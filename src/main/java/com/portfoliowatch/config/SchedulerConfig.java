package com.portfoliowatch.config;

import com.portfoliowatch.service.AccountService;
import com.portfoliowatch.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccountService accountService;

    @Scheduled(cron = "0 30 16 ? * MON-FRI")
    public void weekdayJobs() {
        accountService.regenerateCostBasisMap();
    }

    @Scheduled(cron = "0 30 15 ? * SUN")
    public void weekdayJobs1() {
        accountService.regenerateCostBasisMap();
    }

    @Scheduled(cron = "0 0 0 ? * MON")
    public void weeklyJob() {

    }
}
