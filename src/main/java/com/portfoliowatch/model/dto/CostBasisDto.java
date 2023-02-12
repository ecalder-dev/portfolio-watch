package com.portfoliowatch.model.dto;

import com.portfoliowatch.model.entity.Lot;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CostBasisDto {
    private String symbol;
    private List<Lot> lotList;
    private Double adjustedPrice;
    private Double totalShares;
    private Date latestTransactionDate;
}
