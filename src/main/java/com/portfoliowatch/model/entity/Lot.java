package com.portfoliowatch.model.entity;

import com.portfoliowatch.model.entity.base.AssetAction;
import lombok.Data;
import lombok.NoArgsConstructor;

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
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "lots")
public class Lot implements AssetAction {

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

    @ManyToOne
    @JoinColumn(name = "fk_lot_account")
    private Account account;

    @Column(name = "datetime_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeCreated;

    @Column(name = "datetime_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeUpdated;

    public Lot(Transaction transaction) {
        this.setSymbol(transaction.getSymbol());
        this.setPrice(transaction.getPrice());
        this.setAccount(transaction.getAccount());
        this.setShares(transaction.getShares());
        this.setDateTransacted(transaction.getDateTransacted());
    }

}
