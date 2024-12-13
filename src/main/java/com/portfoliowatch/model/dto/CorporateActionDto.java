package com.portfoliowatch.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfoliowatch.model.entity.CorporateAction;
import com.portfoliowatch.util.enums.CorporateActionType;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CorporateActionDto {

  private Long id;
  private CorporateActionType type;
  private String oldSymbol;
  private String newSymbol;
  private BigDecimal originalPrice;
  private BigDecimal spinOffPrice;
  private BigDecimal ratioAntecedent;
  private BigDecimal ratioConsequent;

  @JsonFormat(pattern = "yyyy-MM-dd")
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
    corporateAction.setSpinOffPrice(this.getSpinOffPrice());
    corporateAction.setRatioAntecedent(this.getRatioAntecedent());
    corporateAction.setRatioConsequent(this.getRatioConsequent());
    corporateAction.setDateOfEvent(this.getDateOfEvent());
    corporateAction.setDatetimeCreated(new Date());
    corporateAction.setDatetimeUpdated(corporateAction.getDatetimeCreated());
    return corporateAction;
  }
}
