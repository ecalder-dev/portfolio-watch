package com.portfoliowatch.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long transactionId;

    @Column(name ="type", length = 3)
    private String type;

    @Column(nullable = false, length = 5)
    private String symbol;

    @Column(name = "shares")
    private Double shares;

    @Column(name = "price")
    private Double price;

    @Column(length = 10)
    private String ratio;

    @Column(name = "new_symbol", length = 5)
    private String newSymbol;

    @Column(name = "execution_priority")
    private Integer executionPriority;

    @ManyToOne
    @JoinColumn(name = "fk_transaction_account")
    private Account account;

    @Column(name = "date_transacted")
    @Temporal(TemporalType.DATE)
    private Date dateTransacted;

    @Column(name = "date_settled")
    @Temporal(TemporalType.DATE)
    private Date dateSettled;

    @Column(name = "date_inserted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeInserted;

    @Column(name = "date_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeUpdated;
}