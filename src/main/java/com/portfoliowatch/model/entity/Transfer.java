package com.portfoliowatch.model.entity;

import com.portfoliowatch.model.entity.base.AssetAction;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Data
@Entity
@Table(name = "transfer")
public class Transfer implements AssetAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5)
    private String symbol;

    @Column
    private Double shares;

    @Column(name = "date_transacted")
    @Temporal(TemporalType.DATE)
    private Date dateTransacted;

    @OneToOne
    @JoinColumn(name = "fk_transfer_old_account")
    private Account fromAccount;

    @OneToOne
    @JoinColumn(name = "fk_transfer_new_account")
    private Account toAccount;

    @Column(name = "datetime_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeCreated;

    @Column(name = "datetime_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeUpdated;

}
