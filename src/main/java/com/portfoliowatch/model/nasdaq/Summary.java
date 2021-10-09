package com.portfoliowatch.model.nasdaq;

import lombok.Data;

@Data
public class Summary {
    String symbol;
    SummaryData summaryData;
    String assetClass;
}
