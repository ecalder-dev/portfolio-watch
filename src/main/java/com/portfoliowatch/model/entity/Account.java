package com.portfoliowatch.model.entity;

import com.portfoliowatch.model.entity.base.BaseEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "accounts")
public class Account implements BaseEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "account_name", nullable = false)
  private String accountName;

  @Column(name = "account_number")
  private String accountNumber;

  @Column(name = "date_opened")
  @Temporal(TemporalType.DATE)
  private LocalDate dateOpened;

  @Column(name = "date_closed")
  @Temporal(TemporalType.DATE)
  private LocalDate dateClosed;

  @Column(name = "is_hidden")
  private Boolean isHidden;

  @Column(name = "datetime_created")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datetimeCreated;

  @Column(name = "datetime_updated")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datetimeUpdated;

  @OneToMany
  @JoinColumn(name = "fk_account_lot")
  private List<Lot> lots;
}
