package com.portfoliowatch.model.nasdaq;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Dividend {

  private Date exOrEffDate;
  private String type;
  private Double amount;
  private String declarationDate;
  private String recordDate;
  private String paymentDate;
}
