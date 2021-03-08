package com.portfoliowatch.model.tdameritrade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
public class TDAmeriPositionDto {
    private String symbol;
    private Double shares;
    private Double costBasis;
    private Double dayChange;
    private Double dayChangePercent;

    public TDAmeriPositionDto(TDAmeriPosition tdAmeriPosition) {
        this.symbol = tdAmeriPosition.getInstrument().getSymbol();
        this.shares = tdAmeriPosition.getSettledLongQuantity();
        this.costBasis = tdAmeriPosition.getAveragePrice();
        this.dayChange = tdAmeriPosition.getCurrentDayProfitLoss();
        this.dayChangePercent = tdAmeriPosition.getCurrentDayProfitLossPercentage();
    }

    public void applyNewShares(TDAmeriPosition tdAmeriPosition) {
        double initCostBasis = this.shares * this.costBasis;
        double addingCostBasis = tdAmeriPosition.getSettledLongQuantity() * tdAmeriPosition.getAveragePrice();
        this.shares += tdAmeriPosition.getSettledLongQuantity();
        this.costBasis = (initCostBasis + addingCostBasis) / this.shares;
    }
}

