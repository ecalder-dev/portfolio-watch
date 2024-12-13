package com.portfoliowatch.config;

import com.portfoliowatch.service.CorporateActionService;
import com.portfoliowatch.service.LotService;
import com.portfoliowatch.service.TransactionService;
import com.portfoliowatch.service.TransferService;
import com.portfoliowatch.service.third.NasdaqAPI;
import com.portfoliowatch.service.third.WallStreetJournalAPI;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Slf4j
@EnableScheduling
@AllArgsConstructor
public class SchedulerConfig {

  private final CacheManager cacheManager;

  private final TransactionService transactionService;

  private final TransferService transferService;

  private final CorporateActionService corporateActionService;

  private final LotService lotService;

  @Scheduled(cron = "0 0 0 ? * MON-SUN", zone = "GMT+5.00")
  public void nightlyJobs() {
    NasdaqAPI.clearCache();
    WallStreetJournalAPI.clearCache();

    // If there are any valid dates in the list, determine the most recent update date
    // and check if it is not before the current date. If it's not, trigger the rebuilding of all
    // lots.
    List<Date> latestUpdates =
        List.of(
            transactionService.getDateOfLastUpdate(),
            transferService.getDateOfLastUpdate(),
            corporateActionService.getDateOfLastUpdate());
    latestUpdates = latestUpdates.stream().filter(Objects::nonNull).collect(Collectors.toList());
    if (latestUpdates.size() > 0) {
      if (!latestUpdates.get(0).before(new Date())) {
        lotService.rebuildAllLots();
      }
    }
  }
}
