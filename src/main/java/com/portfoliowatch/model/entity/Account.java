package com.portfoliowatch.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.portfoliowatch.model.dto.CostBasisDto;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Entity
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

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CostBasisDto> costBasisList;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private double totalAnnualDividends;
}