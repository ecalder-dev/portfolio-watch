package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.CorporateActionDto;
import com.portfoliowatch.service.CorporateActionService;
import com.portfoliowatch.util.exception.NoDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class CorporateActionController {

    @Autowired
    private CorporateActionService corporateActionService;

    @GetMapping("/corporate-actions")
    public List<CorporateActionDto> getAllCorporateActions() {
        return corporateActionService.getAllCorporateActions();
    }

    @GetMapping("/corporate-actions/{id}")
    public ResponseEntity<CorporateActionDto> getCorporateActionById(@PathVariable Long id) {
        CorporateActionDto corporateActionDto = corporateActionService.getCorporateActionById(id);
        if (corporateActionDto != null) {
            return ResponseEntity.ok(corporateActionDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/corporate-actions")
    public ResponseEntity<CorporateActionDto> createCorporateAction(@RequestBody CorporateActionDto corporateActionDto) {
        try {
            CorporateActionDto createdCorporateAction = corporateActionService.createCorporateAction(corporateActionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCorporateAction);
        } catch (NoDataException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/corporate-actions")
    public ResponseEntity<CorporateActionDto> updateCorporateAction(@RequestBody CorporateActionDto corporateActionDto) {
        try {
            return ResponseEntity.ok(corporateActionService.updateCorporateAction(corporateActionDto));
        } catch (NoDataException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/corporate-actions/{id}")
    public ResponseEntity<Void> deleteCorporateAction(@PathVariable Long id) {
        try {
            corporateActionService.deleteCorporateAction(id);
            return ResponseEntity.noContent().build();
        } catch (NoDataException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
