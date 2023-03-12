package com.portfoliowatch.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuoteDto {
    private String symbol;
    private Double currentPrice;
    private Long averageVolume;
    private Double dollarChange;
    private Double percentChange;
    private String companyName;
    private String industry;
    private String sector;
    private Boolean isEtf;
}
