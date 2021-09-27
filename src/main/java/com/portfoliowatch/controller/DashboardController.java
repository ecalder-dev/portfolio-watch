package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.QuoteDto;
import com.portfoliowatch.model.wsj.WSJInstrument;
import com.portfoliowatch.service.PortfolioStatsService;
import com.portfoliowatch.service.WSJService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/dashboard")
@RestController
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private PortfolioStatsService portfolioStatsService;

    @Autowired
    private WSJService WSJService;

    @GetMapping("quotes")
    public ResponseEntity<List<QuoteDto>> getQuoteList() {
        List<QuoteDto> data;
        HttpStatus httpStatus;
        try {
            data = portfolioStatsService.getQuoteList();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("sector-spread")
    public ResponseEntity<Map<String, BigDecimal>> getSectorSpread() {
        Map<String, BigDecimal> data;
        HttpStatus httpStatus;
        try {
            data = portfolioStatsService.getSectorSpread();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("indices")
    public ResponseEntity<List<WSJInstrument>> getIndices() {
        List<WSJInstrument> data;
        HttpStatus httpStatus;
        try {
            data = WSJService.getIndices();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            logger.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

}
