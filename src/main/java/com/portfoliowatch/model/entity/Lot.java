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
@Table(name = "lots")
public class Lot {

    @Id
    @Column(name = "lot_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long lotId;

    @Column(nullable = false, length = 5)
    private String symbol;

    @Column
    private Double shares;

    @Column
    private Double price;

    @Column(name = "date_transacted")
    @Temporal(TemporalType.DATE)
    private Date dateTransacted;

    @Column(name = "date_inserted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeInserted;

    @Column(name = "date_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeUpdated;

    @ManyToOne
    @JoinColumn(name = "fk_lot_account")
    private Account account;
}
