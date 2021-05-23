package com.portfoliowatch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Getter @Setter
@Entity @ToString
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long accountId;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "date_opened")
    @Temporal(TemporalType.DATE)
    private Date dateOpened;

    @Column(name = "date_inserted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeInserted;

    @Column(name = "date_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeUpdated;

}