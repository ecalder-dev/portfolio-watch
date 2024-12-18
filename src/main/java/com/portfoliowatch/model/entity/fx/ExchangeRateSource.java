package com.portfoliowatch.model.entity.fx;

import com.portfoliowatch.util.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "exchange_rate_source")
public class ExchangeRateSource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "source_name", length = 100)
  private String sourceName;

  @Column(name = "source_url", length = 250)
  private String sourceUrl;

  @Column(name = "date_header", length = 25)
  private String dateHeader;

  @Column(name = "rate_header", length = 25)
  private String rateHeader;

  @Enumerated(EnumType.STRING)
  @Column(name = "from_currency", length = 3)
  private Currency fromCurrency;

  @Enumerated(EnumType.STRING)
  @Column(name = "to_currency", length = 3)
  private Currency toCurrency;

  @Column(name = "datetime_created")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datetimeCreated;

  @Column(name = "datetime_updated")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datetimeUpdated;
}
