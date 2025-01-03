package com.portfoliowatch.model.dto.fx;

import com.portfoliowatch.model.entity.fx.ExchangeRate;
import com.portfoliowatch.util.enums.Currency;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateDto {
  private LocalDate date;
  private Currency fromCurrency;
  private Currency toCurrency;
  private BigDecimal rate;

  public ExchangeRateDto(ExchangeRate exchangeRate) {
    date = exchangeRate.getExchangeRateId().getDate();
    fromCurrency = exchangeRate.getExchangeRateId().getFromCurrency();
    toCurrency = exchangeRate.getExchangeRateId().getToCurrency();
    rate = exchangeRate.getRate();
  }
}
