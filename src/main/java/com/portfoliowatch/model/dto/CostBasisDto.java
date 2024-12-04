package com.portfoliowatch.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class CostBasisDto {
    private String symbol;
    private List<LotDto> lotList;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private BigDecimal adjustedPrice;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private BigDecimal totalShares;
    private Date latestTransactionDate;
}
