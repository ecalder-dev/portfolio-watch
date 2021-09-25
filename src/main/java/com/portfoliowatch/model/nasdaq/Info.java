package com.portfoliowatch.model.nasdaq;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Info {
    private String symbol;
    private String companyName;
    private String stockType;
    private String exchange;
    private InfoData primaryData;
    private InfoData secondaryData;
    private KeyStats keyStats;
}

@Getter @Setter
class InfoData {
    private String lastSalePrice;
    private String netChange;
    private String percentageChange;
    private String deltaIndicator;
    private String lastTradeTimestamp;
}

@Getter @Setter
class KeyStats {
    @SerializedName("Volume")
    private LabelValue volume;
    @SerializedName("ExpenseRatio")
    private LabelValue expenseRatio;
}
