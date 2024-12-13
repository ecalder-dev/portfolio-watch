package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.QuoteDto;
import com.portfoliowatch.model.wsj.WSJInstrument;
import com.portfoliowatch.service.QuotesService;
import com.portfoliowatch.service.third.WallStreetJournalAPI;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RequestMapping("/api")
@RestController()
public class QuotesController {

  private final QuotesService quotesService;

  @GetMapping("/quotes")
  public ResponseEntity<List<QuoteDto>> getQuotes(@RequestParam Set<String> symbols) {
    List<QuoteDto> data;
    HttpStatus httpStatus;
    try {
      data = quotesService.getQuotes(symbols);
      httpStatus = HttpStatus.OK;
    } catch (Exception e) {
      data = null;
      log.error(e.getLocalizedMessage(), e);
      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return new ResponseEntity<>(data, httpStatus);
  }

  @GetMapping("/quotes/{symbol}")
  public ResponseEntity<QuoteDto> getQuote(@PathVariable("symbol") String symbol) {
    QuoteDto data;
    HttpStatus httpStatus;
    try {
      data = quotesService.getQuote(symbol);
      httpStatus = HttpStatus.OK;
    } catch (Exception e) {
      data = null;
      log.error(e.getLocalizedMessage(), e);
      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return new ResponseEntity<>(data, httpStatus);
  }

  @GetMapping("/quotes/indices")
  public ResponseEntity<List<WSJInstrument>> getIndices() {
    List<WSJInstrument> data;
    HttpStatus httpStatus;
    try {
      data = WallStreetJournalAPI.getIndices();
      httpStatus = HttpStatus.OK;
    } catch (Exception e) {
      data = null;
      log.error(e.getLocalizedMessage(), e);
      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return new ResponseEntity<>(data, httpStatus);
  }
}
