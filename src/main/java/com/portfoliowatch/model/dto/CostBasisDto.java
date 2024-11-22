package com.portfoliowatch.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class CostBasisDto {
    private String symbol;
    private List<LotDto> lotList;
    private Double adjustedPrice;
    private Double totalShares;
    private Date latestTransactionDate;
}
