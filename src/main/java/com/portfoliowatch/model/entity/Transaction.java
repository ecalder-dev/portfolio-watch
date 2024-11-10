package com.portfoliowatch.model.entity;

import com.portfoliowatch.model.entity.base.AssetAction;
import com.portfoliowatch.util.enums.TransactionType;
import lombok.Data;

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
import java.util.Date;

@Data
@Entity
@Table(name = "transactions")
public class Transaction implements AssetAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double price;

    @Column(nullable = false, length = 5)
    private String symbol;

    @Column
    private Double shares;

    @Column(name = "date_transacted")
    @Temporal(TemporalType.DATE)
    private Date dateTransacted;

    @Enumerated(EnumType.STRING)
    @Column(name ="type", length = 5)
    private TransactionType type;

    @ManyToOne
    @JoinColumn(name = "fk_transaction_account")
    private Account account;

    @Column(name = "datetime_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeCreated;

    @Column(name = "datetime_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeUpdated;

}