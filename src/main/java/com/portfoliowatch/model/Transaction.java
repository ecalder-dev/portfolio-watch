package com.portfoliowatch.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter @Setter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long transactionId;

    private String type;

    @Column(nullable = false)
    private String symbol;

    @Column(name = "shares")
    private Double shares;

    @Column(name = "price")
    private Double price;

    @ManyToOne
    @JoinColumn(name="account_id")
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