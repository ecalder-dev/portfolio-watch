package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.AggregatedAnnualSaleDto;
import com.portfoliowatch.model.dto.LotSaleDto;
import com.portfoliowatch.service.LotSaleService;
import java.util.List;
import java.util.Map;
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
  public ResponseEntity<List<LotSaleDto>> getAllLotSalesByYear(@PathVariable("year") Integer year) {
    return ResponseEntity.ok(lotSaleService.getLotSalesByYear(year));
  }

  @GetMapping("/aggregate/{year}")
  public ResponseEntity<AggregatedAnnualSaleDto> getAggregatedLotSales(
      @PathVariable("year") Integer year) {
    return ResponseEntity.ok(lotSaleService.getAggregatedAnnualSaleByYear(year));
  }

  @GetMapping("/aggregate")
  public ResponseEntity<Map<Integer, AggregatedAnnualSaleDto>> getAllAggregatedLotSales() {
    return ResponseEntity.ok(lotSaleService.getAllAggregatedAnnualSales());
  }

  @GetMapping("/tax-years")
  public ResponseEntity<List<Integer>> getAllAvailableTaxYears() {
    return ResponseEntity.ok(lotSaleService.getAllAvailableTaxYears());
  }
}
