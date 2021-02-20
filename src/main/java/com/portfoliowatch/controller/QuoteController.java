package com.portfoliowatch.controller;

import com.portfoliowatch.model.Quote;
import com.portfoliowatch.service.EmailService;
import com.portfoliowatch.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/quote")
@RestController
public class QuoteController {

    @Autowired
    QuoteService quoteService;

    @Autowired
    EmailService emailService;

    @GetMapping("")
    public ResponseEntity<Quote> getQuote(@RequestParam String ticker) {
        ResponseEntity<Quote> responseEntity;
        try {
            responseEntity = new ResponseEntity<>(quoteService.getQuote(ticker).get(), HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Quote>> getQuotes(@RequestParam List<String> tickers) {
        ResponseEntity<List<Quote>> responseEntity;
        try {
            List<Quote> quotes = quoteService.getQuotes(tickers).get();
            if (quotes.size() < tickers.size()) {
                responseEntity = new ResponseEntity<>(quotes, HttpStatus.PARTIAL_CONTENT);
            } else {
                responseEntity = new ResponseEntity<>(quotes, HttpStatus.OK);
            }
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    @GetMapping("/nasdaq")
    public ResponseEntity<Boolean> update() {
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<String> email(@RequestParam Long portfolioId) {
        ResponseEntity<String> responseEntity;
        try {
            this.emailService.sendReport("dev.edw.calderon@gmail.com", portfolioId);
            responseEntity = new ResponseEntity<>("Email sent.", HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}
