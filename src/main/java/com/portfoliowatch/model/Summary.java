package com.portfoliowatch.model;

import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.model.financialmodelingprep.FMPProfile;
import com.portfoliowatch.util.Lot;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Summary {
    private String symbol;
    private Double currentPrice;
    private Long averageVolume;
    private Double dollarChange;
    private Double percentChange;
    private String companyName;
    private String industry;
    private String sector;
    private Boolean isEtf;
    private Double totalShares;
    private Double totalCurrentPrice;

    public Summary(@NotNull FMPProfile fmpProfile, @NotNull Lot lot) {
        this.symbol = fmpProfile.getSymbol();
        this.currentPrice = fmpProfile.getPrice();
        this.averageVolume = fmpProfile.getVolAvg();
        this.dollarChange = fmpProfile.getChanges();
        this.companyName = fmpProfile.getCompanyName();
        this.industry = fmpProfile.getIndustry();
        this.sector = fmpProfile.getSector();
        this.isEtf = fmpProfile.getIsEtf();
        this.percentChange = (currentPrice / (currentPrice - dollarChange)) - 1;
        this.totalShares = lot.getShares();
        this.totalCurrentPrice = lot.getShares() * fmpProfile.getPrice();
    }
}
