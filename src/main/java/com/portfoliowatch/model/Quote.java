package com.portfoliowatch.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class Quote {
    private String ticker;
    private Double highestPrice;
    private Double lowestPrice;
    private Double openingPrice;
    private Double closingPrice;
    private Double change;
    private String changePercent;
    private Integer volume;
    private Date datePulled;
}
