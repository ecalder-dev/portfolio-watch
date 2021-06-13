package com.portfoliowatch.model.financialmodelingprep;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class FMPNews {
    private String symbol;
    private Date publishedDate;
    private String title;
    private String image;
    private String site;
    private String text;
    private String url;
}
