package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.AggregatedAnnualSaleDto;
import com.portfoliowatch.service.LotSaleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/lots/sales")
@Slf4j
@AllArgsConstructor
@RestController
public class LotSaleController {

  private final LotSaleService lotSaleService;

  @GetMapping("/{year}")
  public ResponseEntity<AggregatedAnnualSaleDto> getAggregatedLotSalesByYear(
      @PathVariable("year") Integer year) {
    return ResponseEntity.ok(lotSaleService.getAggregatedLotSalesByYear(year));
  }
}
