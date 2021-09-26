package com.portfoliowatch.model.nasdaq;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Info {
    private String symbol;
    private String companyName;
    private String stockType;
    private String exchange;
    private InfoData primaryData;
    private InfoData secondaryData;
    private KeyStats keyStats;
    private String assetClass;
}
