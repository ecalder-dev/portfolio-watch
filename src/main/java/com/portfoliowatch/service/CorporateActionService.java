package com.portfoliowatch.service;

import com.portfoliowatch.model.dto.CorporateActionDto;
import com.portfoliowatch.model.entity.CorporateAction;
import com.portfoliowatch.repository.CorporateActionRepository;
import com.portfoliowatch.util.ErrorHandler;
import com.portfoliowatch.util.exception.NoDataException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CorporateActionService {

  private final CorporateActionRepository corporateActionRepository;

  public List<CorporateActionDto> getAllCorporateActions() {
    return corporateActionRepository.findAll().stream()
        .map(CorporateActionDto::new)
        .collect(Collectors.toList());
  }

  public CorporateActionDto getCorporateActionById(Long id) {
    Optional<CorporateAction> corporateAction = corporateActionRepository.findById(id);
    return corporateAction.map(CorporateActionDto::new).orElse(null);
  }

  public CorporateActionDto createCorporateAction(CorporateActionDto corporateActionDto)
      throws NoDataException {
    ErrorHandler.validateNonNull(
        corporateActionDto.getType(), "CorporateActionDto's type should not be null.");
    ErrorHandler.validateNonNull(
        corporateActionDto.getOldSymbol(), "CorporateActionDto's oldSymbol should not be null.");
    ErrorHandler.validateNonNull(
        corporateActionDto.getDateOfEvent(),
        "CorporateActionDto's dateOfEvent should not be null.");

    CorporateAction corporateAction = corporateActionDto.generateCorporateAction();
    corporateAction.setDatetimeCreated(new Date());
    corporateAction.setDatetimeUpdated(new Date());

    return new CorporateActionDto(corporateActionRepository.save(corporateAction));
  }

  public CorporateActionDto updateCorporateAction(CorporateActionDto corporateActionDto)
      throws NoDataException {
    ErrorHandler.validateNonNull(
        corporateActionDto.getId(), "CorporateActionDto's id should not be null.");
    ErrorHandler.validateNonNull(
        corporateActionDto.getType(), "CorporateActionDto's type should not be null.");
    ErrorHandler.validateNonNull(
        corporateActionDto.getOldSymbol(), "CorporateActionDto's oldSymbol should not be null.");
    ErrorHandler.validateNonNull(
        corporateActionDto.getDateOfEvent(),
        "CorporateActionDto's dateOfEvent should not be null.");

    Long id = corporateActionDto.getId();
    CorporateAction corporateAction =
        corporateActionRepository
            .findById(id)
            .orElseThrow(() -> new NoDataException("Corporate Action not found with id " + id));

    corporateAction.setOldSymbol(corporateActionDto.getOldSymbol());
    corporateAction.setNewSymbol(corporateActionDto.getNewSymbol());
    corporateAction.setOriginalPrice(corporateActionDto.getOriginalPrice());
    corporateAction.setSpinOffPrice(corporateActionDto.getSpinOffPrice());
    corporateAction.setRatioAntecedent(corporateActionDto.getRatioAntecedent());
    corporateAction.setRatioConsequent(corporateActionDto.getRatioConsequent());
    corporateAction.setDatetimeUpdated(new Date());
    corporateAction.setDateOfEvent(corporateActionDto.getDateOfEvent());

    return new CorporateActionDto(corporateActionRepository.save(corporateAction));
  }

  public void deleteCorporateAction(Long id) throws NoDataException {
    CorporateAction corporateAction =
        corporateActionRepository
            .findById(id)
            .orElseThrow(() -> new NoDataException("Corporate Action not found with id " + id));
    corporateActionRepository.delete(corporateAction);
  }

  public Date getDateOfLastUpdate() {
    return corporateActionRepository.findLatestDatetimeUpdated();
  }
}
