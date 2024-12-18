package com.portfoliowatch.controller;

import com.portfoliowatch.service.fx.ExchangeRateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {

  private final ExchangeRateService exchangeRateService;

  @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadCSV(
      @PathVariable Long id, @RequestParam("file") MultipartFile file) {
    // Validate the file
    if (!file.isEmpty()) {
      exchangeRateService.readCSVUpload(id, file);
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok().build();
  }

  @DeleteMapping(value = "/reset")
  public ResponseEntity<Void> reset() {
    exchangeRateService.resetRecords();
    return ResponseEntity.accepted().build();
  }
}
