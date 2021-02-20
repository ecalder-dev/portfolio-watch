package com.portfoliowatch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {



    @Scheduled(cron = "0 15 0 ? * MON-FRI")
    public void weekdayJobs() {
    }

    @Scheduled(cron = "0 0 0 ? * MON")
    public void weeklyJob() {

    }
}
