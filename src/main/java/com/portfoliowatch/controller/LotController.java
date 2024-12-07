package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.model.dto.schwab.ProcessRequest;
import com.portfoliowatch.model.entity.base.BaseEvent;
import com.portfoliowatch.service.LotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RequestMapping("/api")
@Slf4j
@AllArgsConstructor
@RestController
public class LotController {

    private final LotService lotService;

    @GetMapping("/lots/cost-basis")
    public ResponseEntity<List<CostBasisDto>> getCostBasisList() {
        List<CostBasisDto> data = null;
        HttpStatus httpStatus;
        try {
            data = lotService.getCostBasis();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("/lots/owned")
    public ResponseEntity<Set<String>> getOwnedSymbols() {
        Set<String> data = null;
        HttpStatus httpStatus;
        try {
            data = lotService.getOwnedStocks();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("/lots/reset")
    public ResponseEntity<Void> reset() {
        lotService.rebuildAllLots();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lots/process/schwab/")
    public ResponseEntity<List<BaseEvent>> processSchwabInput(@RequestBody ProcessRequest processRequest, @RequestParam(defaultValue = "true") boolean isSimulate) {
        return ResponseEntity.ok(lotService.processSchwabTransactions(processRequest.getBrokerageTransactions(), processRequest.getTargetAccountId(), processRequest.getTransferAccountId(), isSimulate));
    }


}
