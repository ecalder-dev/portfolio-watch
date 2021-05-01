package com.portfoliowatch.model.tdameritrade;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
public class TDAmeriTransaction {

    private String type;
    private String subAccount;
    private String settlementDate;
    private Double netAmount;
    private String transactionDate;
    private String transactionSubType;
    private String transactionId;
    private Boolean cashBalanceEffectFlag;
    private String description;
    private TDAmeriTransactionItem transactionItem;
}
