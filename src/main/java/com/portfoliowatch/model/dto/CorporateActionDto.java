package com.portfoliowatch.model.dto;

import com.portfoliowatch.model.entity.CorporateAction;
import com.portfoliowatch.util.enums.CorporateActionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class CorporateActionDto {

    private Long id;
    private CorporateActionType type;
    private String oldSymbol;
    private String newSymbol;
    private Double originalPrice;
    private Double spinOffPrice;
    private Double ratioAntecedent;
    private Double ratioConsequent;
    private Date dateOfEvent;

    public CorporateActionDto(CorporateAction corporateAction) {
        this.id = corporateAction.getId();
        this.type = corporateAction.getType();
        this.oldSymbol = corporateAction.getOldSymbol();
        this.newSymbol = corporateAction.getNewSymbol();
        this.originalPrice = corporateAction.getOriginalPrice();
        this.spinOffPrice = corporateAction.getSpinOffPrice();
        this.ratioAntecedent = corporateAction.getRatioAntecedent();
        this.ratioConsequent = corporateAction.getRatioConsequent();
        this.dateOfEvent = corporateAction.getDateOfEvent();
    }

    public CorporateAction generateCorporateAction() {
        CorporateAction corporateAction = new CorporateAction();
        corporateAction.setType(this.getType());
        corporateAction.setOldSymbol(this.getOldSymbol());
        corporateAction.setNewSymbol(this.getNewSymbol());
        corporateAction.setOriginalPrice(this.getOriginalPrice());
        corporateAction.setRatioAntecedent(this.getRatioAntecedent());
        corporateAction.setRatioConsequent(this.getRatioAntecedent());
        corporateAction.setDateOfEvent(this.getDateOfEvent());
        corporateAction.setDatetimeCreated(new Date());
        corporateAction.setDatetimeUpdated(corporateAction.getDatetimeCreated());
        return corporateAction;
    }

}
