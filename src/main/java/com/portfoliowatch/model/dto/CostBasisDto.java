package com.portfoliowatch.model.dto;

import lombok.Data;

@Data
public class CostBasisDto {
    private String symbol;
    private LotList lotList;
    private Double adjustedPrice;
    private Double totalShares;
    private Double totalAnnualDividend;
}
