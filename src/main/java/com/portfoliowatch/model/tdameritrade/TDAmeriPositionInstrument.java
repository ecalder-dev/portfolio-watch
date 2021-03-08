package com.portfoliowatch.model.tdameritrade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TDAmeriPositionInstrument {
    private String assetType;
    private String cusip;
    private String symbol;
}