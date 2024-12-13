package com.portfoliowatch.model.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AggregatedAnnualSaleDto {

  private Integer year;
  private BigDecimal totalRealizedGainLoss;
  private BigDecimal totalRealizedGainLossYen;
  private List<LotSaleDto> soldLots;
}
