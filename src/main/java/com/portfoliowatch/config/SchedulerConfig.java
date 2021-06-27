package com.portfoliowatch.config;

import com.portfoliowatch.service.EmailService;
import com.portfoliowatch.service.TransactionService;
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
    private TransactionService transactionService;

    @Scheduled(cron = "0 30 15 ? * MON-FRI")
    public void weekdayJobs() {
        transactionService.generateAccountLotListMap();
    }

    @Scheduled(cron = "0 30 15 ? * SAT-SUN")
    public void weekendJobs() {
        transactionService.generateAccountLotListMap();
    }

}
