package com.portfoliowatch.model.entity;

import com.portfoliowatch.model.entity.base.Base;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "accounts")
public class Account implements Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "date_opened")
    @Temporal(TemporalType.DATE)
    private Date dateOpened;

    @Column(name = "date_closed")
    @Temporal(TemporalType.DATE)
    private Date dateClosed;

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