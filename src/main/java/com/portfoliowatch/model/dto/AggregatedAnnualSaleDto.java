package com.portfoliowatch.model.dto;

import com.portfoliowatch.util.enums.Currency;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AggregatedAnnualSaleDto {
  private Integer year;
  private Map<Currency, BigDecimal> totalAcquisitionPrice = new HashMap<>();
  private Map<Currency, BigDecimal> totalSoldPrice = new HashMap<>();
  private Map<Currency, BigDecimal> totalRealizedGainLoss = new HashMap<>();
}
