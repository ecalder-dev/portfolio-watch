package com.portfoliowatch.model.entity;

import com.portfoliowatch.util.enums.LotSaleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "lot_sales")
public class LotSale {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false, length = 5)
  private String symbol;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 16)
  private LotSaleType type;

  @Column(nullable = false, name = "acquisition_price", precision = 20, scale = 5)
  private BigDecimal acquisitionPrice;

  @Column(nullable = false, name = "total_acquisition_price", precision = 20, scale = 5)
  private BigDecimal totalAcquisitionPrice;

  @Column(name = "date_acquired")
  @Temporal(TemporalType.DATE)
  private LocalDate dateAcquired;

  @Column(nullable = false, name = "sold_shares", precision = 20, scale = 5)
  private BigDecimal soldShares;

  @Column(nullable = false, name = "sold_price", precision = 20, scale = 5)
  private BigDecimal soldPrice;

  @Column(nullable = false, name = "total_sold_price", precision = 20, scale = 5)
  private BigDecimal totalSoldPrice;

  @Column(name = "date_sold")
  @Temporal(TemporalType.DATE)
  private LocalDate dateSold;

  @Column(nullable = false, name = "tax_year")
  private Integer taxYear;

  @Column(nullable = false, name = "total_price_difference", precision = 20, scale = 5)
  private BigDecimal totalPriceDifference;

  @ManyToOne
  @JoinColumn(name = "fk_lot_sale_lot")
  private Lot lot;

  @Column(name = "datetime_created")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datetimeCreated;

  @Column(name = "datetime_updated")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datetimeUpdated;
}
