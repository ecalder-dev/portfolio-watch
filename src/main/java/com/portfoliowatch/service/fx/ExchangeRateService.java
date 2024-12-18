package com.portfoliowatch.service.fx;

import com.portfoliowatch.model.entity.fx.ExchangeRate;
import com.portfoliowatch.model.entity.fx.ExchangeRateId;
import com.portfoliowatch.model.entity.fx.ExchangeRateSource;
import com.portfoliowatch.repository.fx.ExchangeRateRepository;
import com.portfoliowatch.repository.fx.ExchangeRateSourceRepository;
import com.portfoliowatch.util.enums.Currency;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class ExchangeRateService {

  private final ExchangeRateRepository exchangeRateRepository;
  private final ExchangeRateSourceRepository exchangeRateSourceRepository;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

  public void readCSVUpload(Long exchangeRateSourceId, MultipartFile file) {
    try {
      ExchangeRateSource exchangeRateSource =
          exchangeRateSourceRepository.findById(exchangeRateSourceId).orElse(null);
      if (exchangeRateSource == null) {
        return;
      }
      CSVFormat csvFormat =
          CSVFormat.DEFAULT
              .builder()
              .setIgnoreHeaderCase(true)
              .setTrim(true)
              .setHeader(exchangeRateSource.getDateHeader(), exchangeRateSource.getRateHeader())
              .build();
      Currency fromCurrency = exchangeRateSource.getFromCurrency();
      Currency toCurrency = exchangeRateSource.getToCurrency();
      List<ExchangeRate> exchangeRateList = new ArrayList<>();

      // Initialize the reader for the CSV file
      Reader reader = new InputStreamReader(file.getInputStream());
      CSVParser csvParser = new CSVParser(reader, csvFormat);

      // Process the CSV records
      List<CSVRecord> csvRecords = csvParser.getRecords();

      BigDecimal recentRate = null;
      for (CSVRecord record : csvRecords) {
        String dateStr = record.get(exchangeRateSource.getDateHeader());
        String rateStr = record.get(exchangeRateSource.getRateHeader());
        BigDecimal rate;
        if (dateStr.equalsIgnoreCase(exchangeRateSource.getDateHeader())) {
          continue;
        }

        Date date = dateFormat.parse(dateStr);
        try {
          rate = new BigDecimal(rateStr);
          recentRate = rate;
        } catch (NumberFormatException e) {
          rate = recentRate;
        }
        ExchangeRateId exchangeRateId = new ExchangeRateId(date, fromCurrency, toCurrency);
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setExchangeRateId(exchangeRateId);
        exchangeRate.setRate(rate);
        exchangeRate.setDatetimeCreated(new Date());
        exchangeRate.setDatetimeUpdated(exchangeRate.getDatetimeCreated());
        exchangeRate.setExchangeRateSource(exchangeRateSource);
        exchangeRateList.add(exchangeRate);
      }
      exchangeRateRepository.saveAll(exchangeRateList);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ExchangeRate getExchangeRate(Date date, Currency fromCurrency, Currency toCurrency) {
    ExchangeRateId exchangeRateId = new ExchangeRateId();
    exchangeRateId.setDate(date);
    exchangeRateId.setFromCurrency(fromCurrency);
    exchangeRateId.setToCurrency(toCurrency);
    return getExchangeRate(exchangeRateId);
  }

  public ExchangeRate getExchangeRate(ExchangeRateId exchangeRateId) {
    return exchangeRateRepository.findById(exchangeRateId).orElse(null);
  }

  public Map<Date, BigDecimal> generateDateRateMap() {
    Map<Date, BigDecimal> dateRateMap = new HashMap<>();
    List<ExchangeRate> exchangeRateList = exchangeRateRepository.findAll();
    for (ExchangeRate exchangeRate : exchangeRateList) {
      dateRateMap.put(exchangeRate.getExchangeRateId().getDate(), exchangeRate.getRate());
    }
    return dateRateMap;
  }

  public void resetRecords() {
    exchangeRateRepository.deleteAll();
  }
}
