package com.portfoliowatch.model.dto.schwab;

import java.util.List;
import lombok.Data;

@Data
public class ProcessRequest {
  private Long targetAccountId;
  private Long transferAccountId;
  private List<BrokerageTransaction> brokerageTransactions;
}
