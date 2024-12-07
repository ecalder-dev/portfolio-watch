package com.portfoliowatch.model.entity;

import com.portfoliowatch.model.entity.base.BaseEvent;
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


import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "corporate_actions")
public class CorporateAction implements BaseEvent {

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

    @Column(name = "original_price", precision = 20, scale = 5)
    private BigDecimal originalPrice;

    @Column(name = "spin_off_price", precision = 20, scale = 5)
    private BigDecimal spinOffPrice;

    @Column(nullable = false, name = "ratio_antecedent", precision = 20, scale = 7)
    private BigDecimal ratioAntecedent;

    @Column(nullable = false, name = "ratio_consequent", precision = 20, scale = 7)
    private BigDecimal ratioConsequent;

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
