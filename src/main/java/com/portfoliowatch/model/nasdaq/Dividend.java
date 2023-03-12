package com.portfoliowatch.model.nasdaq;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter @Getter
public class Dividend {

    private Date exOrEffDate;
    private String type;
    private Double amount;
    private String declarationDate;
    private String recordDate;
    private String paymentDate;

}
