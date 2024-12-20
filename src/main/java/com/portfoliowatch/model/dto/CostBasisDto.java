package com.portfoliowatch.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CostBasisDto {
  private String symbol;
  private List<LotDto> lotList;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private BigDecimal adjustedPrice;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private BigDecimal totalShares;

  private LocalDate latestTransactionDate;
}
