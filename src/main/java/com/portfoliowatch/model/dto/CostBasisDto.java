package com.portfoliowatch.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.portfoliowatch.model.dto.deserializer.LocalDateDeserializer;
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
  private BigDecimal adjustedPrice;
  private BigDecimal totalShares;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate latestTransactionDate;
}
