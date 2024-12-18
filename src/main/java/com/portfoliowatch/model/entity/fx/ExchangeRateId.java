package com.portfoliowatch.model.entity.fx;

import com.portfoliowatch.util.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateId implements Serializable {

  @Temporal(TemporalType.DATE)
  private Date date;

  @Enumerated(EnumType.STRING)
  @Column(name = "from_currency", length = 3)
  private Currency fromCurrency;

  @Enumerated(EnumType.STRING)
  @Column(name = "to_currency", length = 3)
  private Currency toCurrency;
}
