package com.portfoliowatch.model.nasdaq;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DividendProfile {

  private LocalDate exDividendDate;
  private LocalDate dividendPaymentDate;
  private String yield;
  private Double annualizedDividend;
  private Double payoutRatio;
}
