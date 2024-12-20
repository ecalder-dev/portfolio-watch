package com.portfoliowatch.model.entity.base;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface AssetAction extends BaseEvent {

  String getSymbol();

  void setSymbol(String symbol);

  BigDecimal getShares();

  void setShares(BigDecimal shares);

  LocalDate getDateTransacted();

  void setDateTransacted(LocalDate dateTransacted);
}
