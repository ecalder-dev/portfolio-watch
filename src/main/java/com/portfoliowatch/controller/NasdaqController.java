package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.QuoteDto;
import com.portfoliowatch.model.nasdaq.NasdaqData;
import com.portfoliowatch.model.nasdaq.NasdaqDividendProfile;
import com.portfoliowatch.model.wsj.WSJInstrument;
import com.portfoliowatch.service.NasdaqService;
import com.portfoliowatch.service.PortfolioStatsService;
import com.portfoliowatch.service.WSJService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequestMapping("/api/nasdaq")
@RestController
public class NasdaqController {

    private static final Logger logger = LoggerFactory.getLogger(NasdaqController.class);

    @Autowired
    private NasdaqService nasdaqService;

    @GetMapping("dividend-profile")
    public ResponseEntity<NasdaqDividendProfile> getDividendProfile(@RequestParam String symbol) {
        NasdaqDividendProfile data;
        HttpStatus httpStatus;
        try {
            data = nasdaqService.getDividendProfile(symbol);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("dividend-profiles")
    public ResponseEntity<Map<String, NasdaqDividendProfile>> getDividendProfiles(@RequestParam Set<String> symbol) {
        Map<String, NasdaqDividendProfile> data;
        HttpStatus httpStatus;
        try {
            data = nasdaqService.getDividendProfiles(symbol);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

}
