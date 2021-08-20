package com.portfoliowatch.model.financialmodelingprep;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class FMPNews {
    private Date publishedDate;
    private String title;
    private String image;
    private String site;
    private String text;
    private String url;
    private Set<String> mentionedSymbols = new HashSet<>();
}
