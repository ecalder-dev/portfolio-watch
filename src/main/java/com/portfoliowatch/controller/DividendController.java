package com.portfoliowatch.controller;

import com.portfoliowatch.model.nasdaq.DividendProfile;
import com.portfoliowatch.service.NasdaqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RequestMapping("/api/dividend")
@RestController
public class DividendController {

    private static final Logger logger = LoggerFactory.getLogger(DividendController.class);

    @Autowired
    private NasdaqService nasdaqService;

    @GetMapping("profile")
    public ResponseEntity<DividendProfile> getDividendProfile(@RequestParam String symbol) {
        DividendProfile data;
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

    @GetMapping("profiles")
    public ResponseEntity<Map<String, DividendProfile>> getDividendProfiles(@RequestParam Set<String> symbol) {
        Map<String, DividendProfile> data;
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
