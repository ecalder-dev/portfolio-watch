package com.portfoliowatch.model.dto.schwab;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.portfoliowatch.util.parser.SchwabDateDeserializer;
import com.portfoliowatch.util.parser.SchwabMoneyDeserializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BrokerageTransaction {

  @JsonProperty("Date")
  @JsonDeserialize(using = SchwabDateDeserializer.class)
  private LocalDate transactionDate;

  @JsonProperty("Action")
  private TransactionAction action;

  @JsonProperty("Symbol")
  private String symbol;

  @JsonProperty("Quantity")
  private BigDecimal quantity;

  @JsonProperty("Price")
  @JsonDeserialize(using = SchwabMoneyDeserializer.class)
  private BigDecimal price;
}
