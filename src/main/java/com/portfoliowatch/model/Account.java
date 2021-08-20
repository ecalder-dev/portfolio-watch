package com.portfoliowatch.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.util.LotList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CostBasisDto> costBasisList;
}