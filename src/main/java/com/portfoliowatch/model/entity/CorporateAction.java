package com.portfoliowatch.model.entity;

import com.portfoliowatch.model.entity.base.Base;
import com.portfoliowatch.util.enums.CorporateActionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;


import java.util.Date;

@Data
@Entity
@Table(name = "corporate_actions")
public class CorporateAction implements Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name ="type", length = 5)
    private CorporateActionType type;

    @Column(name = "old_symbol")
    private String oldSymbol;

    @Column(name = "new_symbol")
    private String newSymbol;

    @Column(name = "price")
    private Double price;

    @Column(name = "spin_off_price")
    private Double spinOffPrice;

    @Column(name = "ratio_antecedent")
    private Double ratioAntecedent;

    @Column(name = "ratio_consequent")
    private Double ratioConsequent;

    @Column(name = "date_of_event")
    @Temporal(TemporalType.DATE)
    private Date dateOfEvent;

    @Column(name = "datetime_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeCreated;

    @Column(name = "datetime_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetimeUpdated;

}
