package com.portfoliowatch.model.financialmodelingprep;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FMPProfile {
    private String symbol;
    private Double price;
    private Double beta;
    private Long volAvg;
    private Long mktCap;
    private Double lastDiv;
    private String range;
    private Double changes;
    private String companyName;
    private String currency;
    private String cik;
    private String isin;
    private String cusip;
    private String exchange;
    private String exchangeShortName;
    private String industry;
    private String website;
    private String description;
    private String ceo;
    private String sector;
    private String country;
    private String fullTimeEmployees;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zip;
    private Double dcfDiff;
    private String dcf;
    private String image;
    private String ipoDate;
    private Boolean defaultImage;
    private Boolean isEtf;
    private Boolean isActivelyTrading;
}