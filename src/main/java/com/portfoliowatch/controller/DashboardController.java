package com.portfoliowatch.controller;

import com.portfoliowatch.api.WallStreetJournalAPI;
import com.portfoliowatch.model.dto.QuoteDto;
import com.portfoliowatch.model.wsj.WSJInstrument;
import com.portfoliowatch.service.DashboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/dashboard")
@Slf4j
@AllArgsConstructor
@RestController
public class DashboardController {

    private final DashboardService portfolioStatsService;

    @GetMapping("quotes")
    public ResponseEntity<List<QuoteDto>> getQuoteList() {
        List<QuoteDto> data;
        HttpStatus httpStatus;
        try {
            data = portfolioStatsService.getQuoteList();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            log.error(e.getLocalizedMessage(), e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @GetMapping("indices")
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
