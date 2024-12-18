package com.portfoliowatch.model.entity.fx;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "exchange_rate")
public class ExchangeRate {

  @EmbeddedId private ExchangeRateId exchangeRateId;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal rate;

  @ManyToOne
  @JoinColumn(name = "fk_exchange_rate_source_exchange_rate")
  private ExchangeRateSource exchangeRateSource;

  @Column(name = "datetime_created")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datetimeCreated;

  @Column(name = "datetime_updated")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datetimeUpdated;
}
