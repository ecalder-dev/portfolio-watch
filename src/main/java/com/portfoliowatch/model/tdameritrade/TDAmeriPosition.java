package com.portfoliowatch.model.tdameritrade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
public class TDAmeriPosition {
    private TDAmeriPositionInstrument instrument;
    private Double shortQuantity;
    private Double averagePrice;
    private Double currentDayProfitLoss;
    private Double currentDayProfitLossPercentage;
    private Double longQuantity;
    private Double settledLongQuantity;
    private Double settledShortQuantity;
    private Double maintenanceRequirement;

    public void applyNewShares(TDAmeriPosition tdAmeriPosition) {
        double initCostBasis = this.settledLongQuantity * this.averagePrice;
        double addedCostBasis = tdAmeriPosition.getSettledLongQuantity() * tdAmeriPosition.getAveragePrice();
        this.settledLongQuantity += tdAmeriPosition.getSettledLongQuantity();
        this.averagePrice = (initCostBasis + addedCostBasis) / this.settledLongQuantity;
    }
}