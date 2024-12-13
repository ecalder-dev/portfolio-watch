package com.portfoliowatch.model.entity.base;

import java.math.BigDecimal;
import java.util.Date;

public interface AssetAction extends BaseEvent {

  String getSymbol();

  void setSymbol(String symbol);

  BigDecimal getShares();

  void setShares(BigDecimal shares);

  Date getDateTransacted();

  void setDateTransacted(Date dateTransacted);
}
