package com.portfoliowatch.model.wsj;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class WSJInstrument {
    private String country;
    private Double dailyHigh;
    private Double dailyLow;
    private String exchangeIsoCode;
    private String formattedName;
    private Double lastPrice;
    private Integer mantissa;
    private String symbol;
    private String name;
    private Double priceChange;
    private Double percentChange;
    private String requestSymbol;
    private String ticker;
    private String timestamp;
    private String type;
    private Double weekAgoChange;
    private Double weekAgoPercentChange;
    private Double oneYearHigh;
    private Double oneYearLow;
    private Double yearAgoPercentChange;
    private Double yearToDatePercentChange;
    private String url;

    public WSJInstrument(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }
}
