package com.portfoliowatch.model.tdameritrade;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TDAmeriQuote {
    private String symbol;
    private String description;
    private Double bidPrice;
    private Double bidSize;
    private String bidId;
    private Double askPrice;
    private Double askSize;
    private String askId;
    private Double lastPrice;
    private Double lastSize;
    private String lastId;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double closePrice;
    private Double netChange;
    private Double totalVolume;
    private Double quoteTimeInLong;
    private Double tradeTimeInLong;
    private Double mark;
    private String exchange;
    private String exchangeName;
    private Boolean marginable;
    private Boolean shortable;
    private Double volatility;
    private Double digits;
    @SerializedName("52WkHigh")
    private Double fiftyTwoWkHigh;
    @SerializedName("52WkLow")
    private Double fiftyTwoWkLow;
    private Double peRatio;
    private Double divAmount;
    private Double divYield;
    private String divDate;
    private String securityStatus;
    private Double regularMarketLastPrice;
    private Double regularMarketLastSize;
    private Double regularMarketNetChange;
    private Double regularMarketTradeTimeInLong;
}
