package com.portfoliowatch.config;

import com.portfoliowatch.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    EmailService emailService;

    @Scheduled(cron = "0 30 16 ? * MON-FRI")
    public void weekdayJobs() {
        try {
            emailService.sendReport("dev.edw.calderon@gmail.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 30 15 ? * SUN")
    public void weekdayJobs1() {
        try {
            emailService.sendReport("dev.edw.calderon@gmail.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Scheduled(cron = "0 0 0 ? * MON")
    public void weeklyJob() {

    }
}
