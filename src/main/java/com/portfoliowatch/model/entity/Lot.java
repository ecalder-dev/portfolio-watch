package com.portfoliowatch.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "lots")
public class Lot {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false, precision = 20, scale = 5)
  private BigDecimal price;

  @Column(nullable = false, length = 5)
  private String symbol;

  @Column(nullable = false, precision = 20, scale = 5)
  private BigDecimal shares;

  @Column(name = "date_transacted")
  @Temporal(TemporalType.DATE)
  private Date dateTransacted;

  @ManyToOne
  @JoinColumn(name = "fk_lot_account")
  private Account account;

  @Column(name = "datetime_created")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datetimeCreated;

  @Column(name = "datetime_updated")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datetimeUpdated;
}
