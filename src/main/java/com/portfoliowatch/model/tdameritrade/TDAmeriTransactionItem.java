package com.portfoliowatch.model.tdameritrade;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
public class TDAmeriTransactionItem {

    private String accountId;
    private Double amount;
    private Double cost;
    private TDAmeriInstrument instrument;
}
