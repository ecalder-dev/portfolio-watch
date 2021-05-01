package com.portfoliowatch.model;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModelProperty;
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

    @Column
    @Temporal(TemporalType.DATE)
    private Date dateSettled;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeInserted;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeUpdated;

}