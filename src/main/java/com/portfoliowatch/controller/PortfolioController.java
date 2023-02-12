package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.service.PortfolioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/portfolio")
@Slf4j
@AllArgsConstructor
@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("cost-basis")
    public ResponseEntity<List<CostBasisDto>> getCostBasisList() {
        List<CostBasisDto> data;
        HttpStatus httpStatus;
        try {
            data = portfolioService.getCostBasisList(false);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            log.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }


}
