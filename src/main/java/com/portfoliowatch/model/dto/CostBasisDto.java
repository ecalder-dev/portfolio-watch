package com.portfoliowatch.model.dto;

import com.portfoliowatch.util.LotList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
public class CostBasisDto {
    private String symbol;
    private LotList lotList;
    private Double adjustedPrice;
    private Double totalShares;
}
