package com.portfoliowatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class
Lot {
    private Double shares;
    private Double price;
    private Date dateTransacted;
}
