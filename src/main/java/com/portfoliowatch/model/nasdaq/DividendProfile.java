package com.portfoliowatch.model.nasdaq;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DividendProfile {

  private Date exDividendDate;
  private Date dividendPaymentDate;
  private String yield;
  private Double annualizedDividend;
  private Double payoutRatio;
}
