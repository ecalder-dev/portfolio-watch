package com.portfoliowatch.model.nasdaq;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoData {
  private String lastSalePrice;
  private String netChange;
  private String percentageChange;
  private String deltaIndicator;
  private String lastTradeTimestamp;
}
