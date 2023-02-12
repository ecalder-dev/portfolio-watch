package com.portfoliowatch.model.dto;

import com.portfoliowatch.model.entity.Lot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotDto {
    private Double shares;
    private Double price;
    private Date dateTransacted;
}
