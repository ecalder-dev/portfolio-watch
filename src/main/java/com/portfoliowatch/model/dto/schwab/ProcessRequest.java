package com.portfoliowatch.model.dto.schwab;

import lombok.Data;

import java.util.List;

@Data
public class ProcessRequest {
    private Long targetAccountId;
    private Long transferAccountId;
    private List<BrokerageTransaction> brokerageTransactions;
}
