package com.portfoliowatch.model;

import lombok.*;

import java.util.Date;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class Lot {
    private Double shares;
    private Double price;
    private Date dateTransacted;
}
